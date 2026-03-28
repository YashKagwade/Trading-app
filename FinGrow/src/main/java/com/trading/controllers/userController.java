package com.trading.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trading.domain.USER_ROLE;
import com.trading.domain.VerificationType;
import com.trading.entities.ForgotPasswordToken;
import com.trading.entities.ForgotPasswordTokenRequest;
import com.trading.entities.ResetPasswordRequest;
import com.trading.entities.User;
import com.trading.entities.VerificationCode;
import com.trading.security.ApiResponse;
import com.trading.security.AuthResponse;
import com.trading.services.EmailService;
import com.trading.services.ForgotPasswordService;
import com.trading.services.UserService;
import com.trading.services.VerificationCodeService;
import com.trading.utils.OtpUtils;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
public class userController {
@Autowired
	private UserService userService;
@Autowired
private EmailService emailService;
@Autowired
private VerificationCodeService veirificationcodeservice;
@Autowired
private ForgotPasswordService forgotpasswordservice;

//Access User Profile
@GetMapping("/api/users/profile")
public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception{
	User user=userService.findUserByJwt(jwt);
	
	return new ResponseEntity<User>(user,HttpStatus.OK);
		
}

/*
 * Send OTP to user so they can enable 2FA.

This function DOES NOT enable 2FA.
It only sends verification code.
 * 
 * */
/*
 * It does 5 tasks:

Identify which user is calling (using JWT)

Check if OTP already exists

If not → generate OTP

Save OTP in DB

Send OTP to email
 * */
@PutMapping("/api/users/profile")
public ResponseEntity<User> updateProfile(
        @RequestHeader("Authorization") String jwt,
        @RequestBody User updatedUser) throws Exception {
    User user = userService.findUserByJwt(jwt);
    user.setFullname(updatedUser.getFullname());
    user.setMobile(updatedUser.getMobile());
    User savedUser = userService.updateUser(user);
    return ResponseEntity.ok(savedUser);
}
@PatchMapping("/api/users/enable-twofactor/verify-otp/{otp}")

//Verify OTP and enable 2FA in User table.
public ResponseEntity<User> enableTwoFactorAuthenticaoin(
		@RequestHeader("Authorization") String jwt,
		@PathVariable String otp
		) throws Exception{
	//JWT → email → user
	//user stores registered users
	/*
id
fullname
email
password
twoFactorAuth
role
	 * */
	User user=userService.findUserByJwt(jwt);
	//Fetch OTP record from DB.
	VerificationCode verificationCode=veirificationcodeservice.getVerificationCodeByUser(user);
	if (verificationCode == null) {
	    throw new Exception("OTP not found");
	}
	String sendTo=verificationCode.getVetificationType().equals(VerificationType.EMAIL)?
			verificationCode.getEmail():verificationCode.getMobile();
	
	boolean isVerified=verificationCode.getOtp().equals(otp);
	
	if(isVerified) {
		User updatedUser=userService.enableTwoFactorAuthentication(
				verificationCode.getVetificationType(), sendTo,user);
	
	return new ResponseEntity<User>(updatedUser,HttpStatus.OK);
	}
	throw new Exception("WRONG OTP ENTERED");
}

//_________FORGOT PASSWORD SECTION_______________


@PostMapping("/auth/users/reset-password/send-otp")
public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
        @RequestBody ForgotPasswordTokenRequest req) throws Exception
{

    System.out.println("🔥 HIT FORGOT PASSWORD API");

    User user = userService.findUserProfileByEmail(req.getSendTo());
 // Generate OTP

    String otp = OtpUtils.generateOTP();

    // Generate unique ID
    UUID uuid = UUID.randomUUID();
    String id = uuid.toString();
    ForgotPasswordToken token =
            forgotpasswordservice.findByUser(user.getId());
    if (token == null) {
        // Create new token
        token = forgotpasswordservice.createToken(
                user,
                id,
                otp,
                req.getVerificationType(),
                req.getSendTo()
        );
    }else {
    	 // 🔥 Update existing token
        token.setOtp(otp);
        forgotpasswordservice.save(token);
    }
        if (req.getVerificationType() == VerificationType.EMAIL) {
            emailService.sendVerifiationOtpEmail(
                    user.getEmail(),
                    token.getOtp()
            );
        }
    

    AuthResponse response = new AuthResponse();
    response.setSession(token.getId());
    response.setMessage("Password reset otp sent successfully");

    return new ResponseEntity<>(response, HttpStatus.OK);
}

//GET FORTGOT PASSWORD OTP FROM USER


//This API sends OTP for password reset.
@PatchMapping("/auth/users/reset-password/verify-otp")
public ResponseEntity<ApiResponse> resetPassword(
        @RequestParam String id,
        @RequestBody ResetPasswordRequest request
) throws Exception {

    ForgotPasswordToken token = forgotpasswordservice.findById(id);

    if (token == null) {
        throw new Exception("Session expired");
    }

    boolean isVerified = token.getOtp().equals(request.getOtp());

    if (!isVerified) {
        throw new Exception("WRONG OTP ENTERED");
    }

    // ✅ Update password (replace old one)
    userService.updatePassword(token.getUser(), request.getPassword());

    // ✅ Delete OTP after success (VERY IMPORTANT)
    forgotpasswordservice.deleteToken(token);

    ApiResponse res = new ApiResponse();
    res.setMessage("Password Updated Successfully");

    return new ResponseEntity<>(res, HttpStatus.OK);
}


//Get all users (Admin only)
@GetMapping("/api/admin/users")
public ResponseEntity<List<User>> getAllUsers(
     @RequestHeader("Authorization") String jwt) throws Exception {
 User currentUser = userService.findUserByJwt(jwt);
 
 // Check if user is admin
 if (currentUser.getRole() != USER_ROLE.ROLE_ADMIN) {
     throw new Exception("Access denied. Admin privileges required.");
 }
 
 List<User> users = userService.getAllUsers();
 return ResponseEntity.ok(users);
}

//Delete user by ID (Admin only)
@DeleteMapping("/api/admin/users/{userId}")
public ResponseEntity<Map<String, String>> deleteUser(
     @PathVariable Long userId,
     @RequestHeader("Authorization") String jwt) throws Exception {
 User currentUser = userService.findUserByJwt(jwt);
 
 // Check if user is admin
 if (currentUser.getRole() != USER_ROLE.ROLE_ADMIN) {
     throw new Exception("Access denied. Admin privileges required.");
 }
 
 userService.deleteUser(userId);
 
 Map<String, String> response = new HashMap<>();
 response.put("message", "User deleted successfully");
 return ResponseEntity.ok(response);
}

//Get user details by ID (Admin only)
@GetMapping("/api/admin/users/{userId}")
public ResponseEntity<User> getUserById(
     @PathVariable Long userId,
     @RequestHeader("Authorization") String jwt) throws Exception {
 User currentUser = userService.findUserByJwt(jwt);
 
 // Check if user is admin
 if (currentUser.getRole() != USER_ROLE.ROLE_ADMIN) {
     throw new Exception("Access denied. Admin privileges required.");
 }
 
 User user = userService.findUserById(userId);
 return ResponseEntity.ok(user);
}








	
}
