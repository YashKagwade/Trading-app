package com.trading.controllers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.trading.Repositories.UserRepo;
import com.trading.domain.USER_ROLE;
import com.trading.entities.TwoFactorAuth;
import com.trading.entities.TwoFactorOTP;
import com.trading.entities.User;
import com.trading.security.AuthResponse;
import com.trading.security.CustomUserDetailsService;
import com.trading.security.JwtProvider;

import com.trading.services.EmailService;
import com.trading.services.TwoFactorOtpService;
import com.trading.services.WatchListService;
//import com.trading.utils.OtpUtils;
import com.trading.utils.OtpUtils;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private EmailService emailService;
	@Autowired
	private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepo userRepo;
@Autowired
private TwoFactorOtpService twofactotpservice;
@Autowired
private WatchListService watchlistservice;
 /*  
@Autowired
private EmailService emailService;*/

@SuppressWarnings("unused")
@PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {
    	
	//checks the emails exsts or not in db
    	User isEMailExists=userRepo.findByEmail(user.getEmail());
    	//if exists then thow error
    	if(isEMailExists!=null) {
    		throw new Exception("Email alread used by another account");
    	}
    	//create new user object and set values to it got from parameter
    	User newUser = new User();
    	newUser.setEmail(user.getEmail());
    	newUser.setPassword(passwordEncoder.encode(user.getPassword())); 
    	newUser.setFullname(user.getFullname());
    	newUser.setMobile(user.getMobile());
    	newUser.setRole(USER_ROLE.ROLE_CUSTOMER);
    	//saved new user to db
    	User savedUser = userRepo.save(newUser);
    	  watchlistservice.createWatchlist(savedUser);
    	  
    	//GENERATES THE JWT
    	Authentication auth =
	                new UsernamePasswordAuthenticationToken(
	                        user.getEmail(),
	                     null,
	                     Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
	                );
    	String jwt=JwtProvider.generateToken(auth);


AuthResponse res= new AuthResponse();  
res.setJwt(jwt);
res.setStatus(true);
res.setMessage("Register Success");

return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
    
    
    
@Autowired
private AuthenticationManager authenticationManager;

@PostMapping("/signin")
public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword()
            )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = JwtProvider.generateToken(authentication);

    User user1 = userRepo.findByEmail(user.getEmail());
    
    // 2FA logic (keep same)
    if (user1.getTwoFactorAuth() != null && user1.getTwoFactorAuth().isEnabled()) {

        // ✅ Generate OTP
        String otp = com.trading.utils.OtpUtils.generateOTP();

        // Delete old OTP if exists
        TwoFactorOTP old = twofactotpservice.findByuser(user1.getId());
        if (old != null) {
            twofactotpservice.deleteTwoFactorOtp(old);
        }

        // ✅ Save new OTP
        TwoFactorOTP newOtp =
                twofactotpservice.createTwoFactorOtp(user1, otp, jwt);

        // ✅ SEND EMAIL
        emailService.sendVerifiationOtpEmail(user1.getEmail(), otp);

        AuthResponse res = new AuthResponse();
        res.setMessage("OTP sent to your email");
        res.setTwoFactorAuthEnabled(true);
        res.setSession(newOtp.getId());   // VERY IMPORTANT

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }
    AuthResponse res = new AuthResponse();
    res.setJwt(jwt);
    res.setStatus(true);
    res.setMessage("Login Success");
    res.setRole(user1.getRole().name());
    return new ResponseEntity<>(res, HttpStatus.CREATED);
}




	//requestparameter binds query parameter
	@PostMapping("/two-factor/otp/{otp}")
	//Verify OTP and enable 2FA in User table.
	  public ResponseEntity<AuthResponse> verifySigninOtp(
			@PathVariable String otp, 
			@RequestParam String id) throws Exception{
		//data binding done
		TwoFactorOTP twoFactorOTP=twofactotpservice.findById(id);
		if(twofactotpservice.verifyTwoFactorOtp(twoFactorOTP,otp)) {
			AuthResponse res= new AuthResponse();  
			//sends the jtw token with the response object
			res.setJwt(twoFactorOTP.getJwt());
			res.setStatus(true);
			res.setMessage("Login Success");
			User user = twoFactorOTP.getUser();
			res.setRole(user.getRole().name());
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		throw new Exception("invalid otp");
		
	}
	
	// Add this method to AuthController.java
	@PostMapping("/create-admin")
	public ResponseEntity<?> createAdminUser() {
	    try {
	        // Check if admin already exists
	        User existingAdmin = userRepo.findByEmail("admin@fingrow.com");
	        if (existingAdmin != null) {
	            return ResponseEntity.ok("Admin user already exists");
	        }
	        
	        // Create admin user
	        User admin = new User();
	        admin.setEmail("admin@fingrow.com");
	        admin.setPassword(passwordEncoder.encode("Admin@123"));
	        admin.setFullname("System Administrator");
	        admin.setMobile("9999999999");
	        admin.setRole(USER_ROLE.ROLE_ADMIN);
	        
	        // Initialize 2FA
	        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
	        twoFactorAuth.setEnabled(false);
	        admin.setTwoFactorAuth(twoFactorAuth);
	        
	        userRepo.save(admin);
	        
	        // Create watchlist for admin
	        watchlistservice.createWatchlist(admin);
	        
	        Map<String, String> response = new HashMap<>();
	        response.put("message", "Admin user created successfully");
	        response.put("email", "admin@fingrow.com");
	        response.put("password", "Admin@123");
	        
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error creating admin: " + e.getMessage());
	    }
	}
	
	
	
	
}