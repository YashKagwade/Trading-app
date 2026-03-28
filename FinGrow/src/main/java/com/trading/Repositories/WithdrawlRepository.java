package com.trading.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.trading.entities.Withdrawl;

public interface WithdrawlRepository extends JpaRepository<Withdrawl, Long> {


List<Withdrawl> findByUserId(long id); 
	
	
	
	
}
