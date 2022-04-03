package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EntityScan("com.example.demo.model.persistence")
@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class DemoApplication extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);
	private static final String DEMO_APP_MAIN = "API_MAIN: ";

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {

		log.info(DEMO_APP_MAIN + "Starting demo application");
		SpringApplication.run(DemoApplication.class, args);
	}

}
