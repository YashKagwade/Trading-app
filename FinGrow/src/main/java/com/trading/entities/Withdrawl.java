package com.trading.entities;



import java.time.LocalDateTime;

import com.trading.domain.WithdrawalStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Entity
public class Withdrawl {
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;

private WithdrawalStatus status;

private Long amount;
@ManyToOne
@JoinColumn(name = "user_id")
private User user;

private LocalDateTime date=LocalDateTime.now();

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public WithdrawalStatus getStatus() {
	return status;
}

public void setStatus(WithdrawalStatus status) {
	this.status = status;
}

public Long getAmount() {
	return amount;
}

public void setAmount(Long amount) {
	this.amount = amount;
}

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}

public LocalDateTime getDate() {
	return date;
}

public void setDate(LocalDateTime date) {
	this.date = date;
}

	
	
	
}
