package org.inzight.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI expenseAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Expense Management API")
                        .description("API documentation for Expense Management Application")
                        .version("1.0.0"));
    }
}