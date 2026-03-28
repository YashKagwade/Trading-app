package com.trading.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.trading.entities.Asset;
import com.trading.entities.User;
import com.trading.services.AssetService;
import com.trading.services.UserService;
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/asset")
public class AssetController {

    // Service layer for asset business logic
    // Handles DB operations related to user assets
    @Autowired
    private AssetService assetService;

    // Used to extract authenticated user from JWT
    // Ensures security and user-specific data access
    @Autowired
    private UserService userService;

    // Constructor injection (preferred way in production)
    // Assigns AssetService dependency
    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    // Fetch asset by its primary key ID
    // Used for direct asset lookup

    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAssetById(
            @PathVariable Long assetId
    ) throws Exception {

        // Call service to get asset from DB
        Asset asset = assetService.getAssetById(assetId);

        // Return asset in HTTP response
        return ResponseEntity.ok().body(asset);
    }

    // Fetch specific asset of logged-in user by coinId
    // JWT → User → Asset (secured access)
    @GetMapping("/coin/{coinId}/user")
    public ResponseEntity<Asset> getAssetByUserIdAndCoinId(
            @PathVariable String coinId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        // Extract authenticated user from JWT
        User user = userService.findUserByJwt(jwt);

        // Fetch asset using userId + coinId (composite lookup)
        Asset asset = assetService.findAssetByIdUserIdAndCoinId(
                user.getId(),
                coinId
        );

        // Return asset details
        return ResponseEntity.ok().body(asset);
    }

    // Fetch all assets owned by logged-in user
    // Used to show portfolio/dashboard
    @GetMapping()
    public ResponseEntity<List<Asset>> getAssetsForUser(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        // Extract authenticated user
        User user = userService.findUserByJwt(jwt);

        // Fetch all assets belonging to user
        List<Asset> assets = assetService.getUsersAsset(user.getId());

        // Return complete asset list
        return ResponseEntity.ok().body(assets);
    }
}