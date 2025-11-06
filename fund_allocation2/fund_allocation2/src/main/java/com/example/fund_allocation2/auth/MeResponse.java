package com.example.fund_allocation2.auth;

import lombok.Data;
import java.util.List;

@Data
public class MeResponse {
    private boolean authenticated;
    private String username;
    private List<String> roles;
}

