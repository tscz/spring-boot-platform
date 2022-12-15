package com.github.tscz.spring.platform.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	private String username;

	@Column
	private String password;

	@Column
	private Boolean enabled;

	public User() {
	}

	public User(String username, String password, Boolean enabled) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}

}
