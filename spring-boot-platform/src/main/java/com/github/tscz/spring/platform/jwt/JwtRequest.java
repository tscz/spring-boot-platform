package com.github.tscz.spring.platform.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {

}