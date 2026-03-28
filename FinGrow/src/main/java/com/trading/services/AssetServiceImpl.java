package com.trading.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.Repositories.AssetRepository;
import com.trading.entities.Asset;
import com.trading.entities.Coin;
import com.trading.entities.User;

@Service
public class AssetServiceImpl implements AssetService {
	@Autowired
	AssetRepository assetRepo;
	@Override
	public Asset createAsset(User user, Coin coin, double quantity) {
		  Asset asset = new Asset();
		    asset.setUser(user);
		    asset.setCoin(coin);
		    asset.setQuantity(quantity);
		    asset.setBuyPrice(coin.getCurrentPrice());

		    return assetRepo.save(asset);
	
	}

	@Override
	public Asset getAssetById(Long assetid) throws Exception {
		// TODO Auto-generated method stub
		return assetRepo.findById(assetid)
			.orElseThrow(()->new Exception("asset not found"));
	}

	@Override
	public Asset getAssetByUserIdAndId(Long userId, Long assetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Asset> getUsersAsset(Long userId) {
		
		return assetRepo.findByUserId(userId);
			
	}

	@Override
	public Asset updateAsset(Long assetId, double quantity) throws Exception {
		Asset oldAsset=getAssetById(assetId);
		oldAsset.setQuantity(quantity+oldAsset.getQuantity());
		return assetRepo.save(oldAsset);
	}

	@Override
	public Asset findAssetByIdUserIdAndCoinId(Long userId, String coinId) {
		
		return assetRepo.findByUserIdAndCoinId(userId, coinId);
		
		
		
	
	}

	@Override
	public void deleteAsset(Long assetId) {
		assetRepo.deleteById(assetId);
		
	}

}
