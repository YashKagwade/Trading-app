package com.trading.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.trading.entities.Asset;
import com.trading.entities.Coin;
import com.trading.entities.User;


public interface AssetService {
Asset createAsset(User user, Coin coin, double quantity);

Asset getAssetById(Long assetid) throws Exception;

Asset getAssetByUserIdAndId(Long userId, Long assetId);

List<Asset> getUsersAsset(Long userId);

Asset updateAsset(Long assetId, double quantity) throws Exception;

Asset findAssetByIdUserIdAndCoinId(Long userId, String coinId);

void deleteAsset(Long assetId);


}
