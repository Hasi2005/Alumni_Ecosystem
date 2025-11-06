package com.example.fund_allocation2.controller;

import com.example.fund_allocation2.auth.AuthServiceClient;
import com.example.fund_allocation2.auth.MeResponse;
import com.example.fund_allocation2.models.Allocation;
import com.example.fund_allocation2.services.AllocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/allocations")
public class AllocationViewController {
    private final AuthServiceClient authClient;
    private final AllocationService allocationService;

    @Value("${auth.public.base-url:http://localhost:8081}")
    private String authPublicBaseUrl;

    public AllocationViewController(AuthServiceClient authClient, AllocationService allocationService) {
        this.authClient = authClient;
        this.allocationService = allocationService;
    }

    @GetMapping("/new")
    public String showForm(HttpServletRequest request, Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create allocations");
        }
        model.addAttribute("currentUser", me.getUsername());
        return "allocation_form";
    }

    @GetMapping("/success/{id}")
    public String success(@PathVariable Long id, HttpServletRequest request, Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view this page");
        }
        Allocation allocation = allocationService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!allocation.getAdminUsername().equals(me.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your allocation");
        }
        model.addAttribute("allocation", allocation);
        return "allocation_success";
    }

    @GetMapping("/admin/list")
    public String listForAdmin(HttpServletRequest request, Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view allocations");
        }
        List<Allocation> list = allocationService.getAllocationsForAdmin(me.getUsername());
        model.addAttribute("allocations", list);
        model.addAttribute("currentUser", me.getUsername());
        return "allocations_list";
    }

    @GetMapping("/student/list")
    public String listForStudent(HttpServletRequest request, Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can view their allocations");
        }
        List<Allocation> list = allocationService.getAllocationsForStudent(me.getUsername());
        model.addAttribute("allocations", list);
        model.addAttribute("currentUser", me.getUsername());
        return "allocations_student_list";
    }

    @GetMapping("/student/view/{id}")
    public String studentView(@PathVariable Long id, HttpServletRequest request, Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can view this page");
        }
        Allocation allocation = allocationService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!allocation.getStudentUsername().equals(me.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your allocation");
        }
        model.addAttribute("allocation", allocation);
        model.addAttribute("currentUser", me.getUsername());
        return "allocation_student_view";
    }

    @PostMapping("/save")
    public String save(@RequestParam("studentUsername") String studentUsername,
                       @RequestParam("amount") Double amount,
                       @RequestParam("purpose") String purpose,
                       HttpServletRequest request,
                       Model model) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create allocations");
        }
        // Basic validation
        if (studentUsername == null || studentUsername.isBlank()) {
            model.addAttribute("error", "Student username is required");
            model.addAttribute("currentUser", me.getUsername());
            model.addAttribute("formStudent", studentUsername);
            model.addAttribute("formAmount", amount);
            model.addAttribute("formPurpose", purpose);
            return "allocation_form";
        }
        if (amount == null || amount <= 0) {
            model.addAttribute("error", "Amount must be positive");
            model.addAttribute("currentUser", me.getUsername());
            model.addAttribute("formStudent", studentUsername);
            model.addAttribute("formAmount", amount);
            model.addAttribute("formPurpose", purpose);
            return "allocation_form";
        }
        if (purpose == null || purpose.isBlank()) {
            model.addAttribute("error", "Purpose is required");
            model.addAttribute("currentUser", me.getUsername());
            model.addAttribute("formStudent", studentUsername);
            model.addAttribute("formAmount", amount);
            model.addAttribute("formPurpose", purpose);
            return "allocation_form";
        }
        // Student existence + role check via REST controller logic reproduction
        var lookup = authClient.findUserByUsername(studentUsername.trim());
        if (lookup == null || !lookup.isExists() || lookup.getRoles() == null || !lookup.getRoles().contains("student")) {
            model.addAttribute("error", "Student not found or not a student role");
            model.addAttribute("currentUser", me.getUsername());
            model.addAttribute("formStudent", studentUsername);
            model.addAttribute("formAmount", amount);
            model.addAttribute("formPurpose", purpose);
            return "allocation_form";
        }
        // Build request
        com.example.fund_allocation2.dto.AllocationRequest req = new com.example.fund_allocation2.dto.AllocationRequest();
        req.setStudentUsername(studentUsername.trim());
        req.setAmount(amount);
        req.setPurpose(purpose.trim());
        Allocation saved = allocationService.allocate(req, me.getUsername());
        model.addAttribute("allocation", saved);
        return "allocation_success";
    }
}
