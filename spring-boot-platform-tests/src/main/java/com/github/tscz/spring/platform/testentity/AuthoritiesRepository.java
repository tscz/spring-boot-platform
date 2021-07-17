package com.github.tscz.spring.platform.testentity;

import org.springframework.data.repository.CrudRepository;

import com.github.tscz.spring.platform.auth.Authorities;

public interface AuthoritiesRepository extends CrudRepository<Authorities, String> {

}
