package com.trading.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.trading.entities.Coin;
import com.trading.entities.User;
import com.trading.entities.Watchlist;
import com.trading.services.CoinService;
import com.trading.services.UserService;
import com.trading.services.WatchListService;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/watchlist/")
public class WatchListController {

    // Service layer for watchlist business logic
    // Handles create, fetch and update operations
    @Autowired
    private WatchListService watchlistservice;

    // Service to extract user from JWT
    // Used for authentication & security validation
    @Autowired
    private UserService userservice;

    // Service to fetch coin details from DB/API
    // Used when adding coin to watchlist
    @Autowired
    private CoinService coinservice;


    // Fetch logged-in user's watchlist
    // JWT → User → Watchlist
    @GetMapping("/user")
    public ResponseEntity<Watchlist> getUserWatchList(
            @RequestHeader("Authorization") String jwt) throws Exception {

        // Extract user from JWT token
        User user = userservice.findUserByJwt(jwt);

        // Fetch watchlist using user ID
        Watchlist watchlist = watchlistservice.findUserWatchlist(user.getId());

        // Return watchlist data
        return ResponseEntity.ok(watchlist);
    }


    // Fetch watchlist directly by watchlist ID
    // Mainly useful for admin or debugging
    @GetMapping("/{watchlistId}")
    public ResponseEntity<Watchlist> getWatchlistById(
            @PathVariable Long watchlistId
    ) throws Exception {

        // Find watchlist by primary key ID
        Watchlist watchlist = watchlistservice.findById(watchlistId);

        // Return watchlist
        return ResponseEntity.ok(watchlist);
    }


    // Add or remove a coin from user's watchlist
    // Acts like toggle (add if not present, remove if already exists)
    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId
    ) throws Exception {

        // Extract authenticated user
        User user = userservice.findUserByJwt(jwt);

        // Fetch coin by ID
        Coin coin = coinservice.findById(coinId);

        // Call service method to add/remove coin
        Coin addedCoin = watchlistservice.addItemToWatchlist(coin, user);

        // Return updated coin info
        return ResponseEntity.ok(addedCoin);
    }
}