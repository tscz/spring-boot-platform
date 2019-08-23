package com.github.tscz.spring.platform.auth;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authorities")
public class Authorities implements Serializable {

	@Id
	private String username;

	@Id
	private String authority;

}
