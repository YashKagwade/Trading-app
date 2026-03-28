	package com.trading.controllers;
	
	import java.util.List;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.*;
	
	import com.trading.entities.Coin;
	import com.trading.entities.CreateOrderRequest;
	import com.trading.entities.Order;
	import com.trading.entities.OrderType;
	import com.trading.entities.User;
	import com.trading.services.CoinService;
	import com.trading.services.OrderService;
	import com.trading.services.UserService;
	
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
	@RestController
	@RequestMapping("/api/orders")
	public class OrderController {
	
		@Autowired
	    // Handles business logic for orders (BUY / SELL)
	    private OrderService orderService;
	
	    // Used for extracting authenticated user from JWT
	    @Autowired
	    private UserService userService;
	
	    // Fetches coin data (price, details)
	    @Autowired
	    private CoinService coinService;
	
	
	    // Create and process order (BUY or SELL)
	    // Flow: JWT → User → Coin → Process Order
	    @PostMapping("/pay")
	    public ResponseEntity<Order> payOrderPayment(
	            @RequestHeader("Authorization") String jwt,
	            @RequestBody CreateOrderRequest req
	    ) throws Exception {
	
	        // Extract authenticated user
	        User user = userService.findUserByJwt(jwt);
	
	        // Fetch coin details using coinId
	        Coin coin = coinService.findById(req.getCoinId());
	
	        // Process order (internally calls buyAsset/sellAsset)
	        Order order = orderService.processOrder(
	                coin,
	                req.getQuantity(),
	                req.getOrderType(),
	                user
	        );
	
	        // Return processed order
	        return ResponseEntity.ok(order);
	    }
	
	
	    // Fetch specific order by ID
	    // Security check: Only owner can view order
	    @GetMapping("/{orderId}")
	    public ResponseEntity<Order> getOrderById(
	            @RequestHeader("Authorization") String jwtToken,
	            @PathVariable Long orderId
	    ) throws Exception {
	
	        // Basic token validation
	        if (jwtToken == null) {
	            throw new Exception("Token missing...");
	        }
	
	        // Extract logged-in user
	        User user = userService.findUserByJwt(jwtToken);
	
	        // Fetch order from DB
	        Order order = orderService.getOrderById(orderId);
	
	        // If order does not exist
	        if (order == null) {
	            return ResponseEntity.notFound().build();
	        }
	
	        // Authorization check (user must own order)
	        if (order.getUser().getId() == user.getId()) {
	            return ResponseEntity.ok(order);
	        }
	
	        // If user tries to access someone else's order
	        throw new Exception("You dont have access");
	    }
	
	
	    // Fetch all orders of logged-in user
	    // Optional filters: order type and asset symbol
	    @GetMapping()
	    public ResponseEntity<List<Order>> getAllOrdersForUser(
	            @RequestHeader("Authorization") String jwt,
	            @RequestParam(required = false) OrderType order_type,
	            @RequestParam(required = false) String asset_symbol
	    ) throws Exception {
	
	        // Extract userId from JWT
	        Long userId = userService.findUserByJwt(jwt).getId();
	
	        // Fetch user orders (with optional filters)
	        List<Order> userOrders =
	                orderService.getAllOrdersOfUser(userId, order_type, asset_symbol);
	
	        // Return list of orders
	        return ResponseEntity.ok(userOrders);
	    }
	}