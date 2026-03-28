package com.trading.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.Repositories.ForgotPasswordRepo;
import com.trading.domain.VerificationType;
import com.trading.entities.ForgotPasswordToken;
import com.trading.entities.User;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

	@Autowired
	private ForgotPasswordRepo forgotpassrepo;
	
	

	@Override
	public ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType,
			String sendTo) {
		ForgotPasswordToken token = new ForgotPasswordToken();

	      token.setUser(user);
	      token.setId(id);
	    token.setOtp(otp);
	    token.setVerificationType(verificationType);
	    token.setSendTo(sendTo);

	    return forgotpassrepo.save(token);
	}

	
	@Override
	public ForgotPasswordToken findById(String id) {
		Optional<ForgotPasswordToken> token = forgotpassrepo.findById(id);
		
		return token.orElse(null);
	}
	@Override
	public ForgotPasswordToken findByUser(Long userid) {
		Optional<ForgotPasswordToken> token=forgotpassrepo.findByUserId(userid);
		return token.orElse(null);
	}

	@Override
	public void deleteToken(ForgotPasswordToken tokeken) {
		forgotpassrepo.delete(tokeken);
		// TODO Auto-generated method stub
		
	}


	@Override
	public ForgotPasswordToken save(ForgotPasswordToken token) {
		   return forgotpassrepo.save(token);
	}



	


	
	
}
