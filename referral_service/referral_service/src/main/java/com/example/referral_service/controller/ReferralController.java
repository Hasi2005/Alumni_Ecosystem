package com.example.referral_service.controller;

import com.example.referral_service.dto.ReferralRequest;
import com.example.referral_service.dto.ReferralResponse;
import com.example.referral_service.dto.UpdateStatusRequest;
import com.example.referral_service.model.Referral;
import com.example.referral_service.service.ReferralService;
import com.example.referral_service.auth.AuthServiceClient;
import com.example.referral_service.auth.MeResponse;
import com.example.referral_service.auth.UserLookupResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referrals")
public class ReferralController {

    private final ReferralService referralService;
    private final AuthServiceClient authClient;

    public ReferralController(ReferralService referralService, AuthServiceClient authClient) {
        this.referralService = referralService;
        this.authClient = authClient;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestReferral(@RequestBody ReferralRequest request, HttpServletRequest http) {
        MeResponse me = authClient.me(http);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can request referrals");
        }
        if (request.getAlumniUsername() == null || request.getAlumniUsername().isBlank()) {
            return ResponseEntity.badRequest().body("alumniUsername is required");
        }
        UserLookupResponse user = authClient.findUserByUsername(request.getAlumniUsername().trim());
        if (user == null || !user.isExists() || user.getRoles() == null || !user.getRoles().contains("alumni")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Alumni not found or invalid role");
        }
        Referral saved = referralService.requestReferral(request.getJobId(), me.getUsername(), request.getAlumniUsername().trim());
        return ResponseEntity.ok(new ReferralResponse(saved.getId(), saved.getJobId(), saved.getStudentUsername(), saved.getAlumniUsername(), saved.getStatus()));
    }

    @GetMapping("/student/me")
    public ResponseEntity<?> getReferralsForStudent(HttpServletRequest http) {
        MeResponse me = authClient.me(http);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can view their referrals");
        }
        List<ReferralResponse> list = referralService.getReferralsByStudent(me.getUsername()).stream()
                .map(r -> new ReferralResponse(r.getId(), r.getJobId(), r.getStudentUsername(), r.getAlumniUsername(), r.getStatus()))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/alumni/me")
    public ResponseEntity<?> getReferralsForAlumni(HttpServletRequest http) {
        MeResponse me = authClient.me(http);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("alumni")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only alumni can view their referrals");
        }
        List<ReferralResponse> list = referralService.getReferralsForAlumni(me.getUsername()).stream()
                .map(r -> new ReferralResponse(r.getId(), r.getJobId(), r.getStudentUsername(), r.getAlumniUsername(), r.getStatus()))
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReferralStatus(@PathVariable Long id,
                                                  @RequestBody UpdateStatusRequest request,
                                                  HttpServletRequest http) {
        MeResponse me = authClient.me(http);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("alumni")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only alumni can update referral status");
        }
        Referral updated = referralService.updateReferralStatus(id, request.getStatus());
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new ReferralResponse(updated.getId(), updated.getJobId(), updated.getStudentUsername(), updated.getAlumniUsername(), updated.getStatus()));
    }

    @GetMapping
    public ResponseEntity<?> getAllReferrals(HttpServletRequest http) {
        MeResponse me = authClient.me(http);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can view all referrals");
        }
        List<ReferralResponse> list = referralService.getAllReferrals().stream()
                .map(r -> new ReferralResponse(r.getId(), r.getJobId(), r.getStudentUsername(), r.getAlumniUsername(), r.getStatus()))
                .toList();
        return ResponseEntity.ok(list);
    }
}

