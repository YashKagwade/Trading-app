package com.trading.services;

import com.trading.entities.TwoFactorOTP;
import com.trading.entities.User;

public interface TwoFactorOtpService {

	TwoFactorOTP createTwoFactorOtp(User user, String Otp, String jwt);
	
	/*Creates random unique id

Creates OTP object

Stores:
otp code , jwt, user
Temporarily store login state
Saves in database*/
	//find otp record for specific user
	TwoFactorOTP findByuser(Long userId );
	TwoFactorOTP findById(String id );	
	//compare user entered otp with stored otp
	boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, String otp);
//delets old otp record
	void deleteTwoFactorOtp(TwoFactorOTP oldTwoFactorOtp);	
	
}
