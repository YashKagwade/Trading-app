package com.trading.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class OrderItem {
	
@Id
@GeneratedValue
private Long id;

private double quantity;

@ManyToOne
private Coin coin;
private double buyPrice;
private double sellPrice;
@JsonIgnore
@OneToOne
@JoinColumn(name = "order_id")
private Order order;
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public double getQuantity() {
	return quantity;
}
public void setQuantity(double quantity) {
	this.quantity = quantity;
}
public Coin getCoin() {
	return coin;
}
public void setCoin(Coin coin) {
	this.coin = coin;
}
public double getBuyPrice() {
	return buyPrice;
}
public void setBuyPrice(double buyPrice) {
	this.buyPrice = buyPrice;
}
public double getSellPrice() {
	return sellPrice;
}
public void setSellPrice(double sellPrice) {
	this.sellPrice = sellPrice;
}
public Order getOrder() {
	return order;
}
public void setOrder(Order order) {
	this.order = order;
}


}
