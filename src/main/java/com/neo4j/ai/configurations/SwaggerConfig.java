package com.neo4j.ai.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "'Neo4j AI Service' REST API", version = "1.0",
                description = "'Neo4j AI Service' REST API endpoints",
                contact = @Contact(name = "'Neo4j AI Service' team")
        )
)
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        SpringDocUtils.getConfig().replaceWithClass(LocalDateTime.class, Long.class);
        SpringDocUtils.getConfig().replaceWithClass(LocalDate.class, Long.class);
        SpringDocUtils.getConfig().replaceWithClass(Date.class, Long.class);

        return GroupedOpenApi.builder()
                .group("Neo4jAIService")
                .packagesToScan("com.neo4j.ai")
                .build();
    }

}
