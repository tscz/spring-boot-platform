package com.github.tscz.spring.platform.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	private String username;

	@Column
	private String password;

	@Column
	private Boolean enabled;

}
