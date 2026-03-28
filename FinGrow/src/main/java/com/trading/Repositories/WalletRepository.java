package com.trading.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.entities.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long>  {

	Wallet findByUserId(Long userId);
}
