package com.trading.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.entities.Coin;
import com.trading.entities.Watchlist;

public interface CoinRepo extends JpaRepository<Coin, String>{

	
	//Watchlist findByUserId(Long userid);
	
}
