package com.example.job_service.controller;

import com.example.job_service.dto.ApplicationRequest;
import com.example.job_service.dto.ApplicationResponse;
import com.example.job_service.dto.UpdateStatusRequest;
import com.example.job_service.service.ApplicationService;
import com.example.job_service.auth.AuthServiceClient;
import com.example.job_service.auth.MeResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final AuthServiceClient authClient;

    @Autowired
    public ApplicationController(ApplicationService applicationService, AuthServiceClient authClient) {
        this.applicationService = applicationService;
        this.authClient = authClient;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getApplicationsForCurrentStudent(HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can view their applications");
        }
        List<ApplicationResponse> list = applicationService.getApplicationsByStudentUsername(me.getUsername());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> applyForJob(@RequestBody ApplicationRequest request, HttpServletRequest http) {
        MeResponse me = authClient.me(http);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can apply for jobs");
        }
        ApplicationResponse resp = applicationService.applyForJob(request, me.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request, HttpServletRequest http) {
        MeResponse me = authClient.me(http);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can update application status");
        }
        ApplicationResponse updated = applicationService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<?> getAllApplications(HttpServletRequest http) {
        MeResponse me = authClient.me(http);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can view all applications");
        }
        return ResponseEntity.ok(applicationService.getAllApplications());
    }
}
