package com.corebank.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coreBankOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("CoreBank API")
                        .description("CoreBank API documentation for transactional authorization and balance inquiry.")
                        .version("1.0.0"));
    }
}
