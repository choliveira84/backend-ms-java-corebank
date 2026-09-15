package com.corebank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = com.corebank.infrastructure.CorebankApplication.class)
class CorebankApplicationTests {

    @Test
    void contextLoads() {
        // The Spring application context will attempt to load and connect to
        // the local PostgreSQL, Redis, and RabbitMQ instances defined in application.yml
    }

}
