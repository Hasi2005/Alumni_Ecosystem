package com.example.fund_allocation2.controller;

import com.example.fund_allocation2.auth.AuthServiceClient;
import com.example.fund_allocation2.auth.MeResponse;
import com.example.fund_allocation2.models.Donation;
import com.example.fund_allocation2.services.DonationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Controller
@RequestMapping("/donations")
public class DonationViewController {
    private final AuthServiceClient authClient;
    private final DonationService donationService;

    // Inject publishable key (can be empty in dev; recommend setting via env or application.properties)
    @Value("${stripe.publishable.key:}")
    private String stripePublishableKey;

    @Value("${auth.public.base-url:http://localhost:8081}")
    private String authPublicBaseUrl;

    public DonationViewController(AuthServiceClient authClient, DonationService donationService) {
        this.donationService = donationService;
        this.authClient = authClient;
    }

    @GetMapping("/new")
    public String showForm(HttpServletRequest request, Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("alumni")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only alumni can donate");
        }
        model.addAttribute("currentUser", me.getUsername());
        model.addAttribute("roles", me.getRoles());
        // expose publishable key to template
        model.addAttribute("stripePublishableKey", stripePublishableKey);
        return "donation_form";
    }

    @GetMapping("/list")
    public String listForAlumni(HttpServletRequest request, Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("alumni")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only alumni can view donations");
        }
        List<Donation> list = donationService.getDonationsForAlumni(me.getUsername());
        model.addAttribute("donations", list);
        model.addAttribute("currentUser", me.getUsername());
        model.addAttribute("roles", me.getRoles());
        return "donations_list";
    }
}
