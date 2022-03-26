package com.example.demo.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
//@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.example.demo.model.persistence.repositories")
//@EntityScan("com.example.demo.model.persistence")
public class RepositoryConfiguration {
}
