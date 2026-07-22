package com.alone.nexus.config;

import com.alone.nexus.security.ApiKeyFilter;
import com.alone.nexus.service.AgentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${nexus.security.api-key-header:X-Agent-Key}")
    private String apiKeyHeader;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AgentService agentService) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/agents/register",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/nexus.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new ApiKeyFilter(agentService, apiKeyHeader),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
