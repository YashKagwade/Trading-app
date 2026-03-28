package com.trading.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.entities.PaymentDetails;



public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

	PaymentDetails findByUserId(Long userid);
}
