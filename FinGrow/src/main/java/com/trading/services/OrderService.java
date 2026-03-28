package com.trading.services;

import java.util.List;

import com.trading.entities.Coin;
import com.trading.entities.Order;
import com.trading.entities.OrderItem;
import com.trading.entities.OrderType;
import com.trading.entities.User;

public interface OrderService {

	Order createOrder(User user, OrderItem orderItem, OrderType orderType);

	Order getOrderById(Long orderId) throws Exception;

	List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);

	Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;

	Order sellAsset(Coin coin, double quantity, User user) throws Exception;
	//OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice)throws Exception;
	
	 OrderItem createOrderItem(Coin coin, double quantity,
			double buyPrice, double sellPrice);
	
	
}