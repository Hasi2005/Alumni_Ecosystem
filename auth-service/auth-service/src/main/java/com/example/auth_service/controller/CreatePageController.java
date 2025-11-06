package com.example.auth_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreatePageController {

    @GetMapping("/create")
    public String serveCreatePage() {
        // Redirect to the static resource in src/main/resources/static/create.html
        return "redirect:/create.html";
    }

    @GetMapping("/login")
    public String serveLoginPage() {
        // Redirect to the static resource in src/main/resources/static/login.html
        return "redirect:/login.html";
    }
}
