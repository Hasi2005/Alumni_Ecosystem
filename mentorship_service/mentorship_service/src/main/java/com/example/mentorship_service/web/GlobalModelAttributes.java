package com.example.mentorship_service.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
@Component
public class GlobalModelAttributes {
    @Value("${auth.public.base-url:http://localhost:8081}")
    private String authPublicBaseUrl;

    @ModelAttribute
    public void addGlobals(Model model) {
        model.addAttribute("authPublicBaseUrl", authPublicBaseUrl);
    }
}

