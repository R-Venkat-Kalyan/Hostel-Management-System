package com.example.hms_fe1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
public class UserEntity {

	@Id
	private long id;
	private String name;
	
	private String mail;
	private String password;
	private String repassword;
	
	public UserEntity() {
		
	}
	
	public UserEntity(long id, String name, String mail, String password, String repassword) {
		super();
		this.id = id;
		this.name = name;
		this.mail = mail;
		this.password = password;
		this.repassword = repassword;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRepassword() {
		return repassword;
	}
	public void setRepassword(String repassword) {
		this.repassword = repassword;
	}
	
	
	
	
}
