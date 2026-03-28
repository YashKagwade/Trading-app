package com.trading.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "two_factor_otp")
public class TwoFactorOTP {
	//Stores OTP when a user logs in with 2FA.
	
	/*id
otp
user_id
jwt*/

	
	
    @Id
    private String id;

    private String otp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;   // ✅ FIXED

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String jwt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public User getUser() {     // ✅ FIXED
        return user;
    }

    public void setUser(User user) {   // ✅ FIXED
        this.user = user;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
/*
Why store JWT?

Because after OTP verification system returns the same JWT.  
  
 
*/
}