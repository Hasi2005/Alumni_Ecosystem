package com.example.auth_service.controller;

import com.example.auth_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class SecurityController {
    @Autowired
    UserService userService;

    @GetMapping("/home")
    public String home(){
        return "This is Home";
    }

    @PostMapping("/create")
    public String create(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam(value = "authority", required = false, defaultValue = "student") String authority){
        return  userService.create(username, password, authority);
    }
}