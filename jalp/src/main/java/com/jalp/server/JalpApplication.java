package com.jalp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.github.tscz.spring.platform.Application;
import com.github.tscz.spring.platform.config.ApplicationConfig;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
public class JalpApplication extends Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
