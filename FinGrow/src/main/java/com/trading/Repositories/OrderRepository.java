package com.trading.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
List<Order> findByUserId(long userId);
	
}
