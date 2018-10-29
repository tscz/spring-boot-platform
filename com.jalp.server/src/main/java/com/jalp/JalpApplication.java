package com.jalp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JalpApplication {

	public static void main(String[] args) {
		SpringApplication.run(JalpApplication.class, args);
	}
}
