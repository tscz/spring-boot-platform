package com.github.tscz.spring.platform.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtResponse(@JsonProperty("token") String token) {

}