package com.trading.security;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {

	
    /*
     * Secret key used to sign and verify JWT tokens.
     * 
     * We convert our SECRETKEY string into a secure HMAC key.
     * This key must be kept private.
     */
	
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(JwtConstant.SECRETEKEY.getBytes());


    /*
     * ===============================
     * GENERATE JWT TOKEN
     * ===============================
     * 
     * This method creates a JWT after successful authentication.
     * 
     * It receives Authentication object which contains:
     *  - username (email)
     *  - authorities (roles)
     */
    public static String generateToken(Authentication auth) {

        // Get all roles/authorities assigned to user
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        // Convert roles into comma-separated string
        String roles = populateAuthorities(authorities);

        /*
         * Build JWT token
         * 
         * Token contains:
         *  - Issued time
         *  - Expiration time
         *  - Email (as claim)
         *  - Authorities (as claim)
         *  - Signed with secret key
         */
        String jwt = Jwts.builder()
                .setIssuedAt(new Date())  // token creation time
                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 10 days validity
                .claim("email", auth.getName())  // store email inside token
                .claim("authorities", roles)     // store roles inside token
                .signWith(KEY)                   // sign token with secret key
                .compact();                      // generate final token string

        return jwt;
    }


    /*
     * ===============================
     * EXTRACT EMAIL FROM TOKEN
     * ===============================
     * 
     * This method:
     * 1. Removes "Bearer " prefix if present
     * 2. Validates token signature using secret key
     * 3. Parses claims
     * 4. Returns email stored in token
     */
    public static String getEmailFromToken(String token) {

        // If token comes with "Bearer " prefix, remove it
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        /*
         * Parse and validate token.
         * If token is:
         *  - Expired
         *  - Tampered
         *  - Signed with wrong key
         * It will throw exception automatically.
         */
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)   // verify using same secret key
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Extract email claim
        return claims.get("email", String.class);
    }


    /*
     * ===============================
     * CONVERT AUTHORITIES TO STRING
     * ===============================
     * 
     * This method converts user roles into comma-separated format.
     * Example:
     * ROLE_USER,ROLE_ADMIN
     */
    private static String populateAuthorities(
            Collection<? extends GrantedAuthority> authorities) {

        Set<String> authSet = new HashSet<>();

        for (GrantedAuthority ga : authorities) {
            authSet.add(ga.getAuthority());
        }

        // Join all roles with comma
        return String.join(",", authSet);
    }
}