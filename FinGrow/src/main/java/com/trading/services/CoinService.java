package com.trading.services;


import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.trading.entities.Coin;

public interface CoinService {
	List<Coin> getCoinList(int page);

	String getMarketChart(String coinId, int days);

	String getCoinDetails(String coinId);

	Coin findById(String coinId) throws Exception;

	String searchCoin(String keyword);

	String getTop50CoinsByMarketCapRank() throws InterruptedException, JsonMappingException, JsonProcessingException;

	String getTrendingCoins();

	
}
