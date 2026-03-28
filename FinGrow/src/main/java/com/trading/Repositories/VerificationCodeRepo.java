package com.trading.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.trading.entities.VerificationCode;

public interface VerificationCodeRepo extends JpaRepository<VerificationCode, Long>{

    VerificationCode findByUser_Id(Long userId);

}