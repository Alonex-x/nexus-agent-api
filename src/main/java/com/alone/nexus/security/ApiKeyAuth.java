package com.alone.nexus.security;

import com.alone.nexus.model.Agent;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class ApiKeyAuth extends AbstractAuthenticationToken {

    private final Agent agent;

    public ApiKeyAuth(Agent agent) {
        super(List.of(new SimpleGrantedAuthority("ROLE_AGENT")));
        this.agent = agent;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return agent;
    }

    public Agent getAgent() {
        return agent;
    }
}
