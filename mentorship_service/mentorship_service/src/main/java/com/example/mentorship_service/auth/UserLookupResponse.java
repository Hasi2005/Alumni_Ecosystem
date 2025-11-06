package com.example.mentorship_service.auth;

import lombok.Data;

import java.util.List;

@Data
public class UserLookupResponse {
    private String username;
    private boolean exists;
    private List<String> roles;
    private boolean alumni;
    private boolean student;
    private boolean admin;
}

