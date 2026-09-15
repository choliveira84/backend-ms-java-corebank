package com.corebank.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.corebank")
@EnableScheduling
@EntityScan(basePackages = "com.corebank.domain")
@EnableJpaRepositories(basePackages = "com.corebank.infrastructure.persistence")
public class CorebankApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorebankApplication.class, args);
    }
}
