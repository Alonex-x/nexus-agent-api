package com.alone.nexus.security;

import com.alone.nexus.model.Agent;
import com.alone.nexus.service.AgentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ApiKeyFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_PATTERNS = List.of(
        "/api/v1/agents/register",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/actuator/health",
        "/nexus.html"
);

    private final AgentService agentService;
    private final String apiKeyHeader;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public ApiKeyFilter(AgentService agentService, String apiKeyHeader) {
        this.agentService = agentService;
        this.apiKeyHeader = apiKeyHeader;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(apiKeyHeader);

        try {
            Agent agent = agentService.getAgentByApiKey(apiKey);
            SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuth(agent));
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, request.getRequestURI());
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = Map.of(
                "error", "API Key ausente o invalido",
                "status", 401,
                "timestamp", Instant.now().toString(),
                "path", path
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
