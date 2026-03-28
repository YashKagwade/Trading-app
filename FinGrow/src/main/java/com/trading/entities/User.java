package com.trading.entities;
//import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.domain.USER_ROLE;
import com.trading.domain.VerificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="users")
public class User {

    @Id
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 // User.java - Already has role field
   
    private String fullname;
    
    @Column(unique = true, nullable = false) 
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String mobile;
    
  public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/*Two Factor auth contains 
   * enabled
   * sendTo
   * 
   * 
   * */
    @Embedded
  
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
    @Enumerated(EnumType.STRING)
    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	
	
	public TwoFactorAuth getTwoFactorAuth() {
		return twoFactorAuth;
	}

	public void setTwoFactorAuth(TwoFactorAuth twoFactorAuth) {
		this.twoFactorAuth = twoFactorAuth;
	}

	public USER_ROLE getRole() {
		return role;
	}

	public void setRole(USER_ROLE role) {
		this.role = role;
	}



}
