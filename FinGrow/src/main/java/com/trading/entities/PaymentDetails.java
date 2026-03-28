package com.trading.entities;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.entities.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
@Entity
public class PaymentDetails {
	
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;


private String accountNumber;
private String accountHolderName;
private String ifsc;

private String bankName;
@OneToOne
@JoinColumn(name = "user_id")
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private User user;
public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getAccountNumber() {
	return accountNumber;
}

public void setAccountNumber(String accountNumber) {
	this.accountNumber = accountNumber;
}

public String getAccountHolderName() {
	return accountHolderName;
}

public void setAccountHolderName(String accountHolderName) {
	this.accountHolderName = accountHolderName;
}

public String getIfsc() {
	return ifsc;
}

public void setIfsc(String ifsc) {
	this.ifsc = ifsc;
}

public String getBankName() {
	return bankName;
}

public void setBankName(String bankName) {
	this.bankName = bankName;
}

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}
 
 
 
}
