package com.trading.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.entities.ForgotPasswordToken;
import com.trading.entities.TwoFactorOTP;

public interface ForgotPasswordRepo extends JpaRepository<ForgotPasswordToken, String> {

	Optional<ForgotPasswordToken> findByUserId(Long userId);
	
}
