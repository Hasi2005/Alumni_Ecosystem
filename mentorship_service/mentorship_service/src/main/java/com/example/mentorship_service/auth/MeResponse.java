package com.example.mentorship_service.auth;

import lombok.Data;
import java.util.List;

@Data
public class MeResponse {
    private boolean authenticated;
    private String username;
    private List<String> roles;
}

