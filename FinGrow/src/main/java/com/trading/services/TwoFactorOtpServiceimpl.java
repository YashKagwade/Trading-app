package com.trading.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.Repositories.twoFactorOtpRepository;
import com.trading.entities.TwoFactorOTP;
import com.trading.entities.User;

@Service
public class TwoFactorOtpServiceimpl implements TwoFactorOtpService {

	@Autowired
	private twoFactorOtpRepository twofactotprepo;
	@Override
	public TwoFactorOTP createTwoFactorOtp(User user, String Otp, String jwt) {
		//create random id object and convert it into the srting
		UUID uuid=UUID.randomUUID();
		String id=uuid.toString();
		
		//set values to teofactor otp object
		TwoFactorOTP twoFactorOtp=new TwoFactorOTP();
		twoFactorOtp.setOtp(Otp);
		twoFactorOtp.setId(id);
		twoFactorOtp.setJwt(jwt);
		 twoFactorOtp.setUser(user);   
		// TODO Auto-generated method stub
		return twofactotprepo.save(twoFactorOtp);
	}

	@Override
	public TwoFactorOTP findByuser(Long userId){
		
		return twofactotprepo.findByUser_Id(userId);
	}

	@Override
	public TwoFactorOTP findById(String id) {
	    Optional<TwoFactorOTP> otp = twofactotprepo.findById(id);
	    return otp.orElse(null);
	}

	@Override
	public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, String otp) {

	    if (twoFactorOtp == null) {
	        return false;
	    }

	    return twoFactorOtp.getOtp().equals(otp);
	}
			
	@Override
	public void deleteTwoFactorOtp(TwoFactorOTP oldotp) {
		// TODO Auto-generated method stub
		twofactotprepo.delete(oldotp);
	}

}
/*| Field | Purpose                            |
| ----- | ---------------------------------- |
| id    | Unique OTP record id               |
| otp   | The generated OTP code             |
| jwt   | Temporary JWT created during login |
| user  | Which user this OTP belongs to     |
*/
