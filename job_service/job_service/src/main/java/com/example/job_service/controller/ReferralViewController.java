package com.example.job_service.controller;

import com.example.job_service.auth.AuthServiceClient;
import com.example.job_service.auth.MeResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/referrals")
public class ReferralViewController {
    private final AuthServiceClient authClient;

    @Value("${auth.public.base-url:http://34.66.236.172:8081}")
    private String authPublicBaseUrl;

    @Value("${referral.public.base-url:http://34.66.236.172:8084}")
    private String referralPublicBaseUrl;

    public ReferralViewController(AuthServiceClient authClient) {
        this.authClient = authClient;
    }

    @GetMapping({"/",""})
    public String console(HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        // Redirect UI to new referral service
        return "redirect:" + referralPublicBaseUrl + "/referrals";
    }
}
