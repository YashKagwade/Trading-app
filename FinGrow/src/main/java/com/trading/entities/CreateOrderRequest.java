package com.trading.entities;

/*
 * DTO (Data Transfer Object)
 * Used to receive order request data from frontend.
 *
 * NOTE:
 * This is NOT marked as @Entity because:
 * - It is not stored in database
 * - It does not represent a table
 * - It is only used for API request mapping
 */
public class CreateOrderRequest {

    // Coin identifier sent by frontend (example: "bitcoin")
    // Used to fetch Coin entity from database/service
    private String coinId;

    // Quantity user wants to buy or sell
    // Used for price calculation and asset update
    private double quantity;


    private OrderType orderType;

   
    public String getCoinId() {
        return coinId;
    }

   
    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

   
    public double getQuantity() {
        return quantity;
    }


    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

   
    public OrderType getOrderType() {
        return orderType;
    }

    
    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
}