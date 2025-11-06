package com.example.auth_service.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String target = "/"; // fallback
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String role = auth.getAuthority();
            if ("admin".equalsIgnoreCase(role)) { target = "/test/admin"; break; }
            if ("alumni".equalsIgnoreCase(role)) { target = "/test/alumni"; break; }
            if ("student".equalsIgnoreCase(role)) { target = "/test/student"; break; }
        }
        response.sendRedirect(target);
    }
}

