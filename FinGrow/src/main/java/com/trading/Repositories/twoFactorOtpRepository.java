package com.trading.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.entities.TwoFactorOTP;

public interface twoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String> {

	TwoFactorOTP findByUser_Id(Long userId);
}
