package com.trading.entities;

import com.trading.domain.VerificationType;

public class ForgotPasswordTokenRequest {
	//this class not stored in db
// Just containers for request body data.
// Used when user requests password reset OTP.
	//It only exists during the request.
    private String sendTo;
    private String token;
    private VerificationType verificationType;

    
    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public VerificationType getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(VerificationType verificationType) {
        this.verificationType = verificationType;
    }
}