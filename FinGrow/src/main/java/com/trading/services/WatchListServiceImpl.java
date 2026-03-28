package com.trading.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.Repositories.WatchListRepository;
import com.trading.entities.Coin;
import com.trading.entities.User;
import com.trading.entities.Watchlist;

@Service
public class WatchListServiceImpl implements WatchListService {

	@Autowired
	private WatchListRepository  watchlistrepo;
	
	@Override
	public Watchlist findUserWatchlist(Long userId) throws Exception {
		Watchlist watchlist=watchlistrepo.findByUserId(userId);
		
		if(watchlist==null) {
			throw new Exception("Watchlist Not Found");
		}
		return watchlist;
	}

	@Override
	public Watchlist createWatchlist(User user) {
		Watchlist watchlist = new Watchlist();
		watchlist.setUser(user);

		return watchlistrepo.save(watchlist);
		
	}

	@Override
	public Watchlist findById(Long id) throws Exception {
		Optional<Watchlist> optionalwatchlist=watchlistrepo.findById(id);
		if(optionalwatchlist.isEmpty()) {
			throw new Exception("Watchlist not found for given id");
			
		}
		
		return optionalwatchlist.get();
	}

	@Override
	public Coin addItemToWatchlist(Coin coin, User user) throws Exception {
	    Watchlist watchlist = findUserWatchlist(user.getId());

	    if (watchlist.getCoins().contains(coin)) {
	        watchlist.getCoins().remove(coin);
	    }
	    else {
	        watchlist.getCoins().add(coin);
	    }

	    watchlistrepo.save(watchlist);

	    return coin;
		
	}

}
