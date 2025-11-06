package com.example.auth_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/test")
public class DashboardController {

    @GetMapping({"","/"})
    public String landing(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase("admin"));
        boolean isAlumni = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase("alumni"));
        boolean isStudent = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase("student"));
        if (isAdmin) return "redirect:/test/admin";
        if (isAlumni) return "redirect:/test/alumni";
        if (isStudent) return "redirect:/test/student";
        return "redirect:/login";
    }

    @GetMapping("/student")
    public String student() {
        return "forward:/dash/student.html";
    }

    @GetMapping("/alumni")
    public String alumni() {
        return "forward:/dash/alumni.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "forward:/dash/admin.html";
    }
}
