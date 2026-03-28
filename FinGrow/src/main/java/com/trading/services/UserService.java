package com.trading.services;

import java.util.List;

import com.trading.domain.VerificationType;
import com.trading.entities.User;

public interface UserService {

	public User findUserByJwt(String jwt) throws Exception;
	public User findUserProfileByEmail(String email)throws Exception;
	public User findUserById(Long id) throws Exception;
	
	User updatePassword(User user, String newPassword) throws Exception;
	User enableTwoFactorAuthentication(VerificationType verificationType,String sendTo,User user);	
	
	// Add this new method
	User updateUser(User user) throws Exception;
	// Add these methods to UserServiceImpl.java

	
	public List<User> getAllUsers();

	public void deleteUser(Long userId) throws Exception;
}
