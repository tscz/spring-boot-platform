package com.jalp.server;

import org.springframework.boot.SpringApplication;

import com.github.tscz.spring.platform.Application;
import com.github.tscz.spring.platform.SpringBootPlatformApplication;

@SpringBootPlatformApplication
public class JalpApplication extends Application {

	public static void main(String[] args) {
		SpringApplication.run(JalpApplication.class, args);
	}

}
