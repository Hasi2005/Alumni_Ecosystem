package com.example.referral_service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuthServiceClient {
    private final RestTemplate restTemplate;

    @Value("${auth.internal.base-url:http://localhost:8081}")
    private String authInternalBaseUrl;

    public AuthServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MeResponse me(HttpServletRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            String cookie = request.getHeader("Cookie");
            if (cookie != null) headers.set("Cookie", cookie);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<MeResponse> resp = restTemplate.exchange(
                    authInternalBaseUrl + "/api/auth/me",
                    HttpMethod.GET,
                    entity,
                    MeResponse.class
            );
            return resp.getBody();
        } catch (Exception ex) {
            MeResponse fallback = new MeResponse();
            fallback.setAuthenticated(false);
            return fallback;
        }
    }

    public UserLookupResponse findUserByUsername(String username) {
        try {
            String safe = URLEncoder.encode(username, StandardCharsets.UTF_8);
            ResponseEntity<UserLookupResponse> resp = restTemplate.getForEntity(
                    authInternalBaseUrl + "/api/users/" + safe,
                    UserLookupResponse.class
            );
            return resp.getBody();
        } catch (Exception ex) {
            return null;
        }
    }
}
