package com.trading.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trading.Repositories.OrderItemRepository;
import com.trading.Repositories.OrderRepository;
import com.trading.Repositories.WalletTransactionRepository;
import com.trading.entities.Asset;
import com.trading.entities.Coin;
import com.trading.entities.Order;
import com.trading.entities.OrderItem;
import com.trading.entities.OrderStatus;
import com.trading.entities.OrderType;
import com.trading.entities.User;
import com.trading.entities.Wallet;
import com.trading.entities.WalletTransaction;
import com.trading.entities.WalletTransactionType;

@Service
public class OrderServiceImple implements OrderService{

	@Autowired
	OrderRepository orderepo;
	
	@Autowired
	OrderItemRepository orderItemrepo;
	
	@Autowired
	WalletService waletser;
	
	@Autowired
	AssetService assetService;
	@Autowired
	WalletTransactionRepository wallettransrepo;
	@Override
	public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
		
		double price = orderItem.getCoin().getCurrentPrice() * orderItem.getQuantity();

		Order order = new Order();
		order.setUser(user);
		order.setOrderItem(orderItem);
		order.setOrderType(orderType);
		order.setPrice(BigDecimal.valueOf(price));
		order.setTimestamp(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);

		return orderepo.save(order);
	}

	@Override
	public Order getOrderById(Long orderId) throws Exception {
	
		return orderepo.findById(orderId)
				.orElseThrow(
						()->new Exception("Order not found"));
						
				
	}

	
	@Override
	public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
	    return orderepo.findByUserId(userId);
	}
	

	public OrderItem createOrderItem(Coin coin, double quantity,
    double buyPrice, double sellPrice) {

// Create item object
OrderItem orderItem = new OrderItem();

// Set which coin is involved
orderItem.setCoin(coin);

// Set how many coins
orderItem.setQuantity(quantity);

// Store buy price
orderItem.setBuyPrice(buyPrice);

// Store sell price
orderItem.setSellPrice(sellPrice);

// Save in database
return orderItemrepo.save(orderItem);
}

	@Transactional
	public Order buyAsset(Coin coin, double quantity, User user) throws Exception {
		// In buyAsset method after successful purchase
		
	    if(quantity <= 0)
	        throw new Exception("Quantity must be > 0");

	    double buyPrice = coin.getCurrentPrice();

	    OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, 0);

	    Order order = createOrder(user, orderItem, OrderType.BUY);
	    
	    
	    orderItem.setOrder(order);

	    waletser.payOrderPayment(order, user);
	    Wallet wallet = waletser.payOrderPayment(order, user);
	    // ✅ Create asset after payment
	    assetService.createAsset(user, coin, quantity);

	    order.setStatus(OrderStatus.SUCCESS);
	    Order savedOrder=orderepo.save(order);
	    
	    WalletTransaction txn = new WalletTransaction();
		txn.setWallet(wallet);
		txn.setAmount((long) order.getPrice().doubleValue());
		txn.setType(WalletTransactionType.BUY_ASSET);
		txn.setDate(LocalDate.now());
		txn.setTransferId("ORDER_" + order.getId());
		txn.setPurpose("Buy " + coin.getName());
		wallettransrepo.save(txn);
	    //Creare Asset
	    Asset oldAsset=assetService.findAssetByIdUserIdAndCoinId(order.getUser().getId(),
	    		order.getOrderItem().getCoin().getId());
	    
	    
	    if(oldAsset==null) {
	    	assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
	    }
	    else {
	    	assetService.updateAsset(oldAsset.getId(), quantity);
	    }
	    
	    
	    
	    return savedOrder;
	}
	
	
	@Transactional
		@Override
	
	public Order sellAsset(Coin coin, double quantity, User user) throws Exception {

	    // Validate quantity
	    if (quantity <= 0) {
	        throw new Exception("quantity should be > 0");
	    }

	    // Get current sell price from market
	    double sellPrice = coin.getCurrentPrice();

	    // Find asset owned by user for this coin
	    Asset assetToSell = assetService.findAssetByIdUserIdAndCoinId(
	            user.getId(),
	            coin.getId()
	    );
	    // Get original buy price
	    double buyPrice = assetToSell.getBuyPrice();
	    // If user does not own the asset
	    if (assetToSell!=null) {
	       
	   
	    // Create OrderItem
	    OrderItem orderItem = createOrderItem(
	            coin,
	            quantity,
	            buyPrice,
	            sellPrice
	    );

	    // Create SELL Order
	    Order order = createOrder(user, orderItem, OrderType.SELL);

	    // Link order and orderItem
	    orderItem.setOrder(order);

	    // Check if user has enough quantity
	    if (assetToSell.getQuantity() >= quantity) {

	        // Mark order as success
	        order.setStatus(OrderStatus.SUCCESS);
	        order.setOrderType(OrderType.SELL);

	        // Save order
	        Order savedOrder = orderepo.save(order);

	        // Credit wallet after selling
	        waletser.payOrderPayment(order, user);

	        
	        
	        // Update asset quantity (subtract sold quantity)
	        Asset updatedAsset = assetService.updateAsset(
	                assetToSell.getId(),
	                -quantity
	        );

	        // If remaining asset value is very small, delete asset
	        if (updatedAsset.getQuantity() * coin.getCurrentPrice() <= 1) {
	            assetService.deleteAsset(updatedAsset.getId());
	        }

	        return savedOrder;
	    }

	    throw new Exception("Insufficient quantity to sell");
	}
	    throw new Exception("Asset not found for user");
	}

	
	
	

	@Override
	@Transactional
	public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception {

	    if (orderType.equals(OrderType.BUY)) {
	        return buyAsset(coin, quantity, user);
	    } 
	    else if (orderType.equals(OrderType.SELL)) {
	        return sellAsset(coin, quantity, user);
	    }

	    throw new Exception("invalid order type");
	}

}
