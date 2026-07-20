package com.alone.nexus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${nexus.security.api-key-header:X-Agent-Key}")
    private String apiKeyHeader;

    @Bean
    public OpenAPI nexusOpenApi() {
        final String securitySchemeName = "ApiKeyAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Nexus Agent Management API")
                        .version("1.0")
                        .description("API REST para gestion de agentes de software, misiones y eventos."))
                .servers(List.of(new Server().url("http://localhost:8080").description("Servidor local")))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(apiKeyHeader)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)));
    }
}
