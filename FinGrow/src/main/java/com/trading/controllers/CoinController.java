package com.trading.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.Repositories.CoinRepo;
import com.trading.entities.Coin;
import com.trading.services.CoinService;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/coins")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;
@Autowired
    private CoinRepo coinrepo;
//Add this endpoint to force refresh all coins from API
@GetMapping("/refresh-all")
public ResponseEntity<?> refreshAllCoins() {
 try {
     System.out.println("🔄 Force refreshing all coins from API...");
     
     // Clear existing coins from database
     coinrepo.deleteAll();
     System.out.println("✅ Cleared existing coins from database");
     
     // Fetch fresh top 50 coins
     String result = coinService.getTop50CoinsByMarketCapRank();
     
     // Parse and count
     JsonNode jsonNode = objectMapper.readTree(result);
     int count = jsonNode.size();
     
     System.out.println("✅ Loaded " + count + " coins from API");
     
     Map<String, Object> response = new HashMap<>();
     response.put("message", "Successfully refreshed " + count + " coins");
     response.put("count", count);
     return ResponseEntity.ok(response);
     
 } catch (Exception e) {
     System.err.println("❌ Error refreshing coins: " + e.getMessage());
     Map<String, String> error = new HashMap<>();
     error.put("error", e.getMessage());
     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
 }
}
    @GetMapping("/list")
    public ResponseEntity<?> getCoinList(@RequestParam(required=false, name="page", defaultValue="1") Integer page) {
        try {
            List<Coin> coins = coinService.getCoinList(page);
            return new ResponseEntity<>(coins, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{coinId}/chart")
    public ResponseEntity<?> getMarketChart(
            @PathVariable String coinId,
            @RequestParam("days") int days) {
        try {
            String res = coinService.getMarketChart(coinId, days);
            JsonNode jsonNode = objectMapper.readTree(res);
            return new ResponseEntity<>(jsonNode, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/top50")
    public ResponseEntity<?> getTop50Coins() {
        try {
            String res = coinService.getTop50CoinsByMarketCapRank();
            // Parse string to JsonNode
            JsonNode jsonNode = objectMapper.readTree(res);
            
            // If it's an array, return it directly
            if (jsonNode.isArray()) {
                return ResponseEntity.ok(jsonNode);
            }
            
            return ResponseEntity.ok(jsonNode);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingCoins() {
        try {
            String res = coinService.getTrendingCoins();
            JsonNode jsonNode = objectMapper.readTree(res);
            return new ResponseEntity<>(jsonNode, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 // Add to CoinController.java
    @GetMapping("/refresh/{coinId}")
    public ResponseEntity<?> refreshCoin(@PathVariable String coinId) {
        try {
            // Delete existing coin
            coinrepo.deleteById(coinId);
            // Fetch fresh data
            Coin coin = coinService.findById(coinId);
            return ResponseEntity.ok(coin);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    @GetMapping("/details/{coinId}")
    public ResponseEntity<?> getCoinDetails(@PathVariable String coinId) {
        try {
            String coindetails = coinService.getCoinDetails(coinId);
            JsonNode jsonNode = objectMapper.readTree(coindetails);
            return ResponseEntity.ok(jsonNode);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("coinId", coinId);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}