package com.example.referral_service.controller;

import com.example.referral_service.auth.AuthServiceClient;
import com.example.referral_service.auth.MeResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/referrals")
public class ReferralViewController {
    private final AuthServiceClient authClient;

    @Value("${auth.public.base-url:http://localhost:8081}")
    private String authPublicBaseUrl;

    public ReferralViewController(AuthServiceClient authClient) {
        this.authClient = authClient;
    }

    @GetMapping({"/",""})
    public String console(HttpServletRequest request, Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        java.util.List<String> roles = me.getRoles();
        boolean isStudent = roles != null && roles.contains("student");
        boolean isAlumni = roles != null && roles.contains("alumni");
        boolean isAdmin = roles != null && roles.contains("admin");
        boolean canViewJobs = true; // any authenticated user
        boolean canViewApplications = isStudent || isAdmin;
        boolean canViewReferrals = isStudent || isAlumni || isAdmin;

        model.addAttribute("currentUser", me.getUsername());
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isAlumni", isAlumni);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("canViewJobs", canViewJobs);
        model.addAttribute("canViewApplications", canViewApplications);
        model.addAttribute("canViewReferrals", canViewReferrals);
        return "referrals";
    }
}
