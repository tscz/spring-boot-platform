package com.github.tscz.spring.platform.testentity;

import org.springframework.data.repository.CrudRepository;

import com.github.tscz.spring.platform.auth.User;

public interface UserRepository extends CrudRepository<User, String> {

}
