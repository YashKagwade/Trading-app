package com.trading.services;

import com.trading.entities.Coin;
import com.trading.entities.User;
import com.trading.entities.Watchlist;

public interface WatchListService {

	Watchlist findUserWatchlist(Long userId) throws Exception;

	Watchlist createWatchlist(User user);

	Watchlist findById(Long id) throws Exception;

	Coin addItemToWatchlist(Coin coin, User user) throws Exception;
	
}
