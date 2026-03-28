package com.trading.entities;

import com.trading.domain.VerificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable

//enabled → whether 2FA is active
//sendTo  → EMAIL or MOBILE

//Stores 2FA settings for the user.
public class TwoFactorAuth {
	@Column(nullable = false)
    private boolean enabled = false;
	
	//reference if enum object
    @Enumerated(EnumType.STRING)
    private VerificationType sendTo;

	public boolean isEnabled() {
		return enabled;
	}
	public VerificationType getSendTo() {
		return sendTo;
	}
	public void setSendTo(VerificationType sendTo) {
		this.sendTo = sendTo;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}