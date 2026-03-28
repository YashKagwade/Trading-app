package com.trading.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.trading.Repositories.CoinRepo;
import com.trading.entities.Coin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoinServiceImpl implements CoinService {

    private static final long CACHE_TTL_MS = 10 * 60 * 1000; // 10 minutes
    private List<Coin> coinListCache;
    private long coinListCacheTime;
    private List<Coin> top50Cache;
    private long top50CacheTime;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CoinRepo coinrepo;

    @Override
    public List<Coin> getCoinList(int page) {
        long now = System.currentTimeMillis();
        
        // Use cache if available
        if (coinListCache != null && now - coinListCacheTime < CACHE_TTL_MS) {
            return coinListCache;
        }

        // Try to get from database first
        List<Coin> dbCoins = coinrepo.findAll();
        if (!dbCoins.isEmpty()) {
            coinListCache = dbCoins;
            coinListCacheTime = now;
            return dbCoins;
        }

        // If DB is empty, fetch from API
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=50&page=" + page;
        RestTemplate restTemplate = new RestTemplate();

        try {
            Thread.sleep(1000);
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
                );

            List<Coin> coinList = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<Coin>>() {}
                );

            coinrepo.saveAll(coinList);
            coinListCache = coinList;
            coinListCacheTime = now;
            return coinList;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                if (coinListCache != null) {
                    return coinListCache;
                }
                return coinrepo.findAll();
            }
            throw new RuntimeException(e.getMessage(), e);

        } catch (Exception e) {
            if (coinListCache != null) {
                return coinListCache;
            }
            return coinrepo.findAll();
        }
    }

    @Override
    public String getCoinDetails(String coinId) {
        // ✅ First try to get from database
        Optional<Coin> dbCoin = coinrepo.findById(coinId);
        if (dbCoin.isPresent()) {
            try {
                // Return as JSON string
                return objectMapper.writeValueAsString(dbCoin.get());
            } catch (Exception e) {
                // If serialization fails, continue to API
            }
        }
        
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;
        RestTemplate restTemplate = new RestTemplate();

        try {
            Thread.sleep(500); // Small delay to avoid rate limit
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                System.err.println("⚠️ Rate limit hit for coin details: " + coinId);
                // Return database data if available
                if (dbCoin.isPresent()) {
                    try {
                        return objectMapper.writeValueAsString(dbCoin.get());
                    } catch (Exception ex) {
                        throw new RuntimeException("Rate limit and fallback failed", ex);
                    }
                }
                throw new RuntimeException("Rate limit exceeded. Please try again later.");
            }
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            if (dbCoin.isPresent()) {
                try {
                    return objectMapper.writeValueAsString(dbCoin.get());
                } catch (Exception ex) {
                    throw new RuntimeException(e);
                }
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Coin findById(String coinId) throws Exception {
        // First check database
        Optional<Coin> optionalcoin = coinrepo.findById(coinId);
        
        if (optionalcoin.isPresent()) {
            return optionalcoin.get();
        }

        // If not in DB, fetch from API with retry logic
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;
        RestTemplate restTemplate = new RestTemplate();
        
        int retryCount = 0;
        int maxRetries = 3;
        
        while (retryCount < maxRetries) {
            try {
                Thread.sleep(1000 * (retryCount + 1));
                
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    
                    Coin coin = new Coin();
                    
                    // Basic info
                    coin.setId(jsonNode.path("id").asText());
                    coin.setName(jsonNode.path("name").asText());
                    coin.setSymbol(jsonNode.path("symbol").asText());
                    coin.setImage(jsonNode.path("image").path("large").asText());

                    JsonNode marketData = jsonNode.path("market_data");
                    
                    // Current price
                    if (marketData.has("current_price") && marketData.path("current_price").has("usd")) {
                        coin.setCurrentPrice(marketData.path("current_price").path("usd").asDouble());
                    }
                    
                    // Market cap
                    if (marketData.has("market_cap") && marketData.path("market_cap").has("usd")) {
                        coin.setMarketCap(marketData.path("market_cap").path("usd").asLong());
                    }
                    
                    // Market cap rank
                    if (marketData.has("market_cap_rank")) {
                        coin.setMarketCapRank(marketData.path("market_cap_rank").asInt());
                    }
                    
                    // Total volume
                    if (marketData.has("total_volume") && marketData.path("total_volume").has("usd")) {
                        coin.setTotalVolume(marketData.path("total_volume").path("usd").asLong());
                    }
                    
                    // 24h high/low
                    if (marketData.has("high_24h") && marketData.path("high_24h").has("usd")) {
                        coin.setHigh24h(marketData.path("high_24h").path("usd").asDouble());
                    }
                    if (marketData.has("low_24h") && marketData.path("low_24h").has("usd")) {
                        coin.setLow24h(marketData.path("low_24h").path("usd").asDouble());
                    }
                    
                    // Price changes
                    if (marketData.has("price_change_24h")) {
                        coin.setPriceChange24h(marketData.path("price_change_24h").asDouble());
                    }
                    if (marketData.has("price_change_percentage_24h")) {
                        coin.setPriceChangePercentage24h(marketData.path("price_change_percentage_24h").asDouble());
                    }
                    if (marketData.has("market_cap_change_24h")) {
                        coin.setMarketCapChange24h(marketData.path("market_cap_change_24h").asLong());
                    }
                    if (marketData.has("market_cap_change_percentage_24h")) {
                        coin.setMarketCapChangePercentage24h(marketData.path("market_cap_change_percentage_24h").asDouble());
                    }
                    
                    // Supply
                    if (marketData.has("circulating_supply")) {
                        coin.setCirculatingSupply(marketData.path("circulating_supply").asLong());
                    }
                    if (marketData.has("total_supply")) {
                        coin.setTotalSupply(marketData.path("total_supply").asLong());
                    }
                    if (marketData.has("max_supply")) {
                        coin.setMaxSupply(marketData.path("max_supply").asLong());
                    }
                    
                    // ATH (All Time High)
                    if (marketData.has("ath") && marketData.path("ath").has("usd")) {
                        coin.setAth(marketData.path("ath").path("usd").asDouble());
                    }
                    
                    System.out.println("✅ Saved coin: " + coin.getName() + " (Rank: " + coin.getMarketCapRank() + ")");
                    
                    // Save to database for future use
                    return coinrepo.save(coin);
                }
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 429) {
                    retryCount++;
                    if (retryCount < maxRetries) {
                        System.err.println("⚠️ Rate limit hit, retrying in " + (2000 * retryCount) + "ms...");
                        continue;
                    }
                } else if (e.getStatusCode().value() == 404) {
                    throw new Exception("Coin not found: " + coinId);
                }
                throw new Exception("Error fetching coin from API: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new Exception("Request interrupted");
            } catch (Exception e) {
                throw new Exception("Coin not found in API also: " + e.getMessage());
            }
        }
        
        throw new Exception("Failed to fetch coin after " + maxRetries + " retries");
    }
    @Override
    public String getMarketChart(String coinId, int days) {
        String url = "https://api.coingecko.com/api/v3/coins/" 
                + coinId 
                + "/market_chart?vs_currency=usd&days=" 
                + days;

        RestTemplate restTemplate = new RestTemplate();

        try {
            Thread.sleep(500); // Add delay to avoid rate limit
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                System.err.println("⚠️ Rate limit hit for market chart: " + coinId);
                // Return empty chart data instead of failing
                return "{\"prices\":[],\"market_caps\":[],\"total_volumes\":[]}";
            }
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            // Return empty chart data on any error
            return "{\"prices\":[],\"market_caps\":[],\"total_volumes\":[]}";
        }
    }

    @Override
    public String getTop50CoinsByMarketCapRank() {
        long now = System.currentTimeMillis();
        
        // FORCE REFRESH: Always fetch from API first
        // Comment out the cache and DB checks for now to force fresh data
        
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=50&page=1&sparkline=false";
        RestTemplate restTemplate = new RestTemplate();

        try {
            System.out.println("🌐 Fetching top 50 coins from CoinGecko API...");
            Thread.sleep(1000); // Delay to avoid rate limit
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("✅ API Response received");

            try {
                List<Coin> parsed = objectMapper.readValue(result, new TypeReference<List<Coin>>() {});
                top50Cache = parsed;
                top50CacheTime = now;
                
                // Save to database
                //coinrepo.deleteAll(); // Clear existing
                coinrepo.saveAll(parsed);
                System.out.println("✅ Saved " + parsed.size() + " coins to database");
                
            } catch (Exception e) {
                System.err.println("❌ Error parsing coins: " + e.getMessage());
                e.printStackTrace();
            }

            return result;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                System.err.println("⚠️ Rate limit hit! Falling back to database...");
                List<Coin> dbCoinsFallback = coinrepo.findAll();
                if (!dbCoinsFallback.isEmpty()) {
                    try {
                        System.out.println("📦 Using " + dbCoinsFallback.size() + " coins from database");
                        return objectMapper.writeValueAsString(dbCoinsFallback);
                    } catch (Exception ex) {
                        throw new RuntimeException("Fallback serialization failed", ex);
                    }
                }
                return "[]";
            }
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            List<Coin> dbCoinsFallback = coinrepo.findAll();
            if (!dbCoinsFallback.isEmpty()) {
                try {
                    return objectMapper.writeValueAsString(dbCoinsFallback);
                } catch (Exception ex) {
                    throw new RuntimeException("Fallback serialization failed", ex);
                }
            }
            return "[]";
        }
    }
    @Override
    public String getTrendingCoins() {
        String url = "https://api.coingecko.com/api/v3/search/trending";
        RestTemplate restTemplate = new RestTemplate();

        try {
            Thread.sleep(500);
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                System.err.println("⚠️ Rate limit hit for trending coins");
                return "{\"coins\":[]}";
            }
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            return "{\"coins\":[]}";
        }
    }

    @Override
    public String searchCoin(String keyword) {
        String url = "https://api.coingecko.com/api/v3/search?query=" + keyword;
        RestTemplate restTemplate = new RestTemplate();

        try {
            Thread.sleep(500);
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                System.err.println("⚠️ Rate limit hit for search");
                return "{\"coins\":[]}";
            }
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            return "{\"coins\":[]}";
        }
    }
}