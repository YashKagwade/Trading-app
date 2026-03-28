package com.trading.security;

import lombok.Data;


public class AuthResponse {
//Response classes are used to send structured API responses.
	
//Used during login and signup.	
	private String jwt;
	private boolean status;
	private String message;
	private boolean isTwoFactorAuthEnabled;
	private String session;
	private String role;
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isTwoFactorAuthEnabled() {
		return isTwoFactorAuthEnabled;
	}
	public void setTwoFactorAuthEnabled(boolean isTwoFactorAuthEnabled) {
		this.isTwoFactorAuthEnabled = isTwoFactorAuthEnabled;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}

}
