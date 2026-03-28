package com.trading.entities;

import com.trading.domain.VerificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity


public class ForgotPasswordToken {
/*
  Stores OTP when user resets password. 
  */
//Purpose - Temporary verification for forgot password process.	
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
private String id;
@OneToOne
@JoinColumn(name = "user_id")
private User user;
private String otp;
@Enumerated(EnumType.STRING)
private VerificationType verificationType;
private String sendTo;

public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public User getUser() {
	return user;
}
public void setUser(User user) {
	this.user = user;
}
public String getOtp() {
	return otp;
}
public void setOtp(String otp) {
	this.otp = otp;
}
public VerificationType getVerificationType() {
	return verificationType;
}
public void setVerificationType(VerificationType verificationType) {
	this.verificationType = verificationType;
}
public String getSendTo() {
	return sendTo;
}
public void setSendTo(String sendTo) {
	this.sendTo = sendTo;
}

	
	
}
