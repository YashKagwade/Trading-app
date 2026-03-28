package com.trading.security; 
import com.trading.security.CustomUserDetailsService;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity; 
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; 
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; 
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter; 
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource; 
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.trading.security.*;

@Configuration 
@EnableWebSecurity 
@EnableMethodSecurity 

public class SecurityConfig {
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	@Bean
	 
	    public DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	        authProvider.setUserDetailsService(customUserDetailsService);
	        authProvider.setPasswordEncoder(passwordEncoder());
	        return authProvider;
	    }
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	 @Bean 
	    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { 
	        
	        JwtTokenValidator jwtTokenValidator = new JwtTokenValidator();

	        http
	       
	            .authenticationProvider(authenticationProvider()) // ✅ FIXED
	            .csrf(csrf -> csrf.disable())
	            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	            		.requestMatchers(
	            		        "/auth/signup",
	            		        "/auth/signin",
	            		        "/auth/two-factor/**",
	            		        "/auth/users/**",   // ✅ this must exist
	            		        "/auth/users/reset-password/**",   // ✅ IMPORTANT FIX
	            		        "/api/payment/callback",
	            		        "/api/payment/debug",
	            		        "/auth/create-admin" 
	            		).permitAll()
	            		.anyRequest().authenticated() 
	            	)
	            .addFilterBefore(jwtTokenValidator, UsernamePasswordAuthenticationFilter.class);

	        return http.build(); 
	    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() { 
        CorsConfiguration config = new CorsConfiguration(); 
        config.setAllowCredentials(true); // Set to true to allow credentials
        
        // Allowed origins
        config.setAllowedOriginPatterns(Arrays.asList(
        	    "*"
        	));
        
  
        config.setAllowedMethods(Arrays.asList("*"));
        // Allow all headers
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Expose headers that frontend might need
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        config.setMaxAge(3600L); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); 
        source.registerCorsConfiguration("/**", config);
        return source; 
    } 
    
    //Authentication Manager 
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
}