package com.caidacelestial.entity;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class User implements Serializable {
	private long id;
	private String username;
	private String password;
	private long record;

	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public long getId() {
		return id;
	}
	
	public long getRecord() {
		return record;
	}
	
	public void setPassword(String contraseña) {
		this.password = contraseña;
	}
	
	public void setUsername(String user) {
		this.username = user;
	}
	
	public void setId(long idN) {
		this.id = idN;
	}
	
	public void setRecord(long newRecord) {
		this.record = newRecord;
	}
}
