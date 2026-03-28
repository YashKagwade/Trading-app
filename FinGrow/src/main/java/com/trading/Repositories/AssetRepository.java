package com.trading.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.entities.Asset;
import com.trading.entities.User;
import java.util.List;


public interface AssetRepository extends JpaRepository<Asset, Long> {

	
    List<Asset> findByUserId(Long userId);

    Asset findByUserIdAndCoinId(Long userId, String coinId);
	
}
