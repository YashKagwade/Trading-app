package com.trading.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.entities.Watchlist;

public interface WatchListRepository extends JpaRepository<Watchlist, Long>{

	
	
	Watchlist findByUserId(Long userid);
}
