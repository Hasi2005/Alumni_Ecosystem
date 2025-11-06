package com.example.auth_service.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/api/auth/me")
    public Map<String, Object> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> out = new HashMap<>();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            out.put("authenticated", false);
            return out;
        }
        out.put("authenticated", true);
        out.put("username", auth.getName());
        out.put("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return out;
    }
}

