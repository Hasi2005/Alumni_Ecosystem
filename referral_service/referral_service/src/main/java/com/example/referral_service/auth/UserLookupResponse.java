package com.example.referral_service.auth;

import java.util.List;

public class UserLookupResponse {
    private String username;
    private boolean exists;
    private List<String> roles;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public boolean isExists() { return exists; }
    public void setExists(boolean exists) { this.exists = exists; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}

