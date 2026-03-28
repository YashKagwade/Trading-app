package com.trading.security;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip JWT validation for these paths
        boolean shouldSkip = path.equals("/api/payment/callback") 
                || path.equals("/api/payment/debug")
                || path.startsWith("/api/auth/")
                || path.startsWith("/auth/");
        
        if (shouldSkip) {
            System.out.println("🔓 Skipping JWT validation for: " + path);
        }
        
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = request.getHeader(JwtConstant.JWT_HEADER);
        
        // Log the request path and auth header presence
        System.out.println("🔐 Processing request: " + request.getMethod() + " " + request.getRequestURI());
        System.out.println("Authorization header present: " + (jwt != null));

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            System.out.println("JWT token extracted (first 20 chars): " + jwt.substring(0, Math.min(20, jwt.length())) + "...");

            try {
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRETEKEY.getBytes());

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = String.valueOf(claims.get("email"));
                String authoritiesString = String.valueOf(claims.get("authorities"));

                System.out.println("✅ Token validated for user: " + email);
                System.out.println("Authorities: " + authoritiesString);

                List<GrantedAuthority> authorityList =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesString);

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(email, null, authorityList);

                SecurityContextHolder.getContext().setAuthentication(auth);
                
                System.out.println("✅ Authentication set in SecurityContext");

            } catch (Exception e) {
                System.err.println("❌ Invalid token: " + e.getMessage());
                // Don't throw exception, just don't set authentication
                // This allows the request to continue without authentication
                SecurityContextHolder.clearContext();
            }
        } else {
            System.out.println("⚠️ No valid JWT token found in request");
        }

        filterChain.doFilter(request, response);
    }
}