package com.example.job_service.controller;

import com.example.job_service.auth.AuthServiceClient;
import com.example.job_service.auth.MeResponse;
import org.springframework.beans.factory.annotation.Value;

@org.springframework.stereotype.Controller
@org.springframework.web.bind.annotation.RequestMapping("/applications")
public class ApplicationViewController {
    private final AuthServiceClient authClient;

    @Value("${auth.public.base-url:http://34.66.236.172:8081}")
    private String authPublicBaseUrl;

    public ApplicationViewController(AuthServiceClient authClient) {
        this.authClient = authClient;
    }

    @org.springframework.web.bind.annotation.GetMapping({"/",""})
    public String console(jakarta.servlet.http.HttpServletRequest request, org.springframework.ui.Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        java.util.List<String> roles = me.getRoles();
        boolean isStudent = roles != null && roles.contains("student");
        boolean isAlumni = roles != null && roles.contains("alumni");
        boolean isAdmin = roles != null && roles.contains("admin");
        boolean canViewJobs = true;
        boolean canViewApplications = isStudent || isAdmin;
        boolean canViewReferrals = isStudent || isAlumni || isAdmin;

        model.addAttribute("currentUser", me.getUsername());
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isAlumni", isAlumni);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("canViewJobs", canViewJobs);
        model.addAttribute("canViewApplications", canViewApplications);
        model.addAttribute("canViewReferrals", canViewReferrals);
        return "applications"; // serve src/main/resources/templates/applications.html
    }
}
