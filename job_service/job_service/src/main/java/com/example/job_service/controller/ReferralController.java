package com.example.job_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/referrals")
public class ReferralController {

    @PostMapping("/request")
    public ResponseEntity<?> requestReferral() {
        return ResponseEntity.status(HttpStatus.GONE).body("Referrals moved to http://localhost:8084/api/referrals");
    }

    @GetMapping("/student/me")
    public ResponseEntity<?> getReferralsForStudent() {
        return ResponseEntity.status(HttpStatus.GONE).body("Referrals moved to http://localhost:8084/api/referrals/student/me");
    }

    @GetMapping("/alumni/me")
    public ResponseEntity<?> getReferralsForAlumni() {
        return ResponseEntity.status(HttpStatus.GONE).body("Referrals moved to http://localhost:8084/api/referrals/alumni/me");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReferralStatus() {
        return ResponseEntity.status(HttpStatus.GONE).body("Referrals moved to http://localhost:8084/api/referrals/{id}/status");
    }

    @GetMapping
    public ResponseEntity<?> getAllReferrals() {
        return ResponseEntity.status(HttpStatus.GONE).body("Referrals moved to http://localhost:8084/api/referrals");
    }
}
