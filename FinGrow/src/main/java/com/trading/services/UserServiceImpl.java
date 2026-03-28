package com.trading.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trading.Repositories.UserRepo;
import com.trading.domain.VerificationType;
import com.trading.entities.TwoFactorAuth;
import com.trading.entities.User;
import com.trading.security.JwtProvider;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepo userepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Override
	//JWT → Extract Email → Find User → Return User
	public User findUserByJwt(String jwt) throws Exception {
		String email =JwtProvider.getEmailFromToken(jwt);
		User user=userepo.findByEmail(email);
		if(user==null) {
			throw new Exception("User not found while verifying otp or jwt expired");
		}
		return user;
	}

	@Override
	public User findUserProfileByEmail(String email) throws Exception {
		User user=userepo.findByEmail(email);
		if(user==null) {
			throw new Exception("User not found while verifying otp or jwt expired");
		}
		return user;

	}

	@Override
	public User findUserById(Long id) throws Exception {
	    return userepo.findById(id)
	            .orElseThrow(() -> new Exception("User not found from particular id."));
	}

	

	@Override
	public User updatePassword(User user, String newPassword) {
		// TODO Auto-generated method stub
		user.setPassword(passwordEncoder.encode(newPassword));
	
		return userepo.save(user);
	}
/*
 * Create new TwoFactorAuth object
Enable it
Attach to user
Save user
*/
	@Override
	public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user) {
		
		TwoFactorAuth twoFactorAuth=new TwoFactorAuth();
		twoFactorAuth.setEnabled(true);
		twoFactorAuth.setSendTo(verificationType);
		user.setTwoFactorAuth(twoFactorAuth);
		
		// TODO Auto-generated method stub
		return userepo.save(user);
	}

	@Override
	public User updateUser(User user) throws Exception {
		// First check if user exists
				User existingUser = findUserById(user.getId());
				
				// Update only allowed fields
				if (user.getFullname() != null && !user.getFullname().isEmpty()) {
					existingUser.setFullname(user.getFullname());
				}
				
				if (user.getMobile() != null && !user.getMobile().isEmpty()) {
					existingUser.setMobile(user.getMobile());
				}
				
				// Email and password should be updated through separate methods for security
				// Don't update email or password here
				
				// Save updated user
				return userepo.save(existingUser);
	}

	@Override
	public List<User> getAllUsers() {
	    return userepo.findAll();
	}

	@Override
	public void deleteUser(Long userId) throws Exception {
		  User user = findUserById(userId);
		    if (user == null) {
		        throw new Exception("User not found");
		    }
		    userepo.delete(user);
		
	}

	

}
