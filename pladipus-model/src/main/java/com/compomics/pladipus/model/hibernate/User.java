package com.compomics.pladipus.model.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="users")  
@Entity(name="users")
public class User {
	private Long userId;
	private String userName;
	private String email;
	private String password;
	private boolean active = true;
	private Role role;
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="user_id")
    public Long getUserId()  
    {  
        return userId;  
    }  
    public void setUserId(Long id)  
    {  
        this.userId = id;  
    }  

    @Column(name="user_name", unique=true, nullable=false, length=30)
    public String getUserName() {
    	return userName;
    }
    public void setUserName(String userName) {
    	this.userName = userName;
    }
    
    @Column(name="email")
    public String getEmail() {
    	return email;
    }
    public void setEmail(String email) {
    	this.email= email;
    }
    
    @Column(name="password", nullable=false, length=32)
    public String getPassword() {
    	return password;
    }
    public void setPassword(String password) {
    	this.password = password;
    }
    
    @Column(name="active", nullable=false)
    public boolean getActive() {
    	return active;
    }
    public void setActive(boolean active) {
    	this.active = active;
    }
    
    @Column(name="user_role")
    @Enumerated(EnumType.STRING)
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
    	this.role = role;
    }
}
