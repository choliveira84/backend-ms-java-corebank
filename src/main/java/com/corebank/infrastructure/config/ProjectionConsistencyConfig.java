package com.corebank.infrastructure.config;

import com.corebank.application.event.ProjectionConsistencyPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class ProjectionConsistencyConfig {

    @Bean
    public ProjectionConsistencyPolicy projectionConsistencyPolicy(
            @Value("${corebank.projection.consistency.max-delay:5s}") Duration maxDelay) {
        return new ProjectionConsistencyPolicy(maxDelay, Clock.systemDefaultZone());
    }
}
