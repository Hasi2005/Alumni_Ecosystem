package com.example.auth_service.web;

import com.example.auth_service.models.User;
import com.example.auth_service.repository.UserRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepo userRepo;

    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/{username}")
    public Map<String, Object> findByUsername(@PathVariable String username) {
        Map<String, Object> resp = new HashMap<>();
        User user = userRepo.findByUsername(username).orElse(null);
        resp.put("username", username);
        resp.put("exists", user != null);
        if (user != null) {
            var roles = user.getAuthorities().stream().map(a -> a.getAuthority()).toList();
            resp.put("roles", roles);
            resp.put("alumni", roles.contains("alumni"));
            resp.put("student", roles.contains("student"));
            resp.put("admin",  roles.contains("admin"));
        }
        return resp;
    }
}

