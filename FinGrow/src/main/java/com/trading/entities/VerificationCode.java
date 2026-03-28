package com.trading.entities;

import com.trading.domain.VerificationType;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


//Used for enabling 2FA
/*
 id
otp
user
email
mobile
verificationType
 * */
@Entity
public class VerificationCode {
/*verification_code
-------------------
id
otp
user_id
verification_type
 
  
  
Used when user enables two-factor authentication.  
  
 * */
	
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private long id;
private String otp;
@ManyToOne
@JoinColumn(name = "user_id")
private User user;

private String email;
private String mobile;
private VerificationType vetificationType;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getOtp() {
	return otp;
}
public void setOtp(String otp) {
	this.otp = otp;
}
public User getUser() {
	return user;
}
public void setUser(User user) {
	this.user = user;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getMobile() {
	return mobile;
}
public void setMobile(String mobile) {
	this.mobile = mobile;
}
public VerificationType getVetificationType() {
	return vetificationType;
}
public void setVetificationType(VerificationType vetificationType) {
	this.vetificationType = vetificationType;
}



}
