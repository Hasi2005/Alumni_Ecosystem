package com.example.referral_service.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
@Component
public class GlobalModelAttributes {
    @Value("${auth.public.base-url:http://34.66.236.172:8081}")
    private String authPublicBaseUrl;

    @Value("${job.public.base-url:http://34.66.236.172:8083}")
    private String jobPublicBaseUrl;

    @ModelAttribute
    public void addGlobals(Model model) {
        model.addAttribute("authPublicBaseUrl", authPublicBaseUrl);
        model.addAttribute("jobPublicBaseUrl", jobPublicBaseUrl);
    }
}
