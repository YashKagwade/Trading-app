package com.trading.entities;



import com.trading.domain.PaymentMethod;
import com.trading.domain.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PaymentOrder {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Long amount;
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentmethod;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentstatus;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	private String orderId; // Paytm order id
	   private String paymentId; // Paytm txn id
	   public Long getId() {
		   return id;
	   }
	   public void setId(Long id) {
		   this.id = id;
	   }
	   public Long getAmount() {
		   return amount;
	   }
	   public void setAmount(Long amount) {
		   this.amount = amount;
	   }
	   public PaymentMethod getPaymentmethod() {
		   return paymentmethod;
	   }
	   public void setPaymentmethod(PaymentMethod paymentmethod) {
		   this.paymentmethod = paymentmethod;
	   }
	   public PaymentStatus getPaymentstatus() {
		   return paymentstatus;
	   }
	   public void setPaymentstatus(PaymentStatus paymentstatus) {
		   this.paymentstatus = paymentstatus;
	   }
	   public User getUser() {
		   return user;
	   }
	   public void setUser(User user) {
		   this.user = user;
	   }
	   public String getOrderId() {
		   return orderId;
	   }
	   public void setOrderId(String orderId) {
		   this.orderId = orderId;
	   }
	   public String getPaymentId() {
		   return paymentId;
	   }
	   public void setPaymentId(String paymentId) {
		   this.paymentId = paymentId;
	   }

	

	
}
