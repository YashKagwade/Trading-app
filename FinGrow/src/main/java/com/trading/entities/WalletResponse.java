package com.trading.entities;

import java.math.BigDecimal;

public class WalletResponse {
	
    private Long walletId;     // wallet id
    private String email;      // user email
    private BigDecimal balance;
	public Long getWalletId() {
		return walletId;
	}
	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
