package com.example.fund_allocation2.controller;

import com.example.fund_allocation2.auth.AuthServiceClient;
import com.example.fund_allocation2.auth.MeResponse;
import com.example.fund_allocation2.auth.UserLookupResponse;
import com.example.fund_allocation2.dto.AllocationRequest;
import com.example.fund_allocation2.models.Allocation;
import com.example.fund_allocation2.services.AllocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/allocations")
public class AllocationController {
    private final AllocationService allocationService;
    private final AuthServiceClient authClient;

    public AllocationController(AllocationService allocationService, AuthServiceClient authClient) {
        this.allocationService = allocationService;
        this.authClient = authClient;
    }

    @PostMapping
    public ResponseEntity<?> allocate(@RequestBody AllocationRequest request, HttpServletRequest httpRequest) {
        MeResponse me = authClient.me(httpRequest);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can create allocations");
        }
        if (request.getStudentUsername() == null || request.getStudentUsername().isBlank()) {
            return ResponseEntity.badRequest().body("studentUsername is required");
        }
        // Validate student exists and has role student in auth-service
        UserLookupResponse user = authClient.findUserByUsername(request.getStudentUsername().trim());
        if (user == null || !user.isExists() || (user.getRoles() == null || !user.getRoles().contains("student"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student not found or invalid role");
        }
        Allocation saved = allocationService.allocate(request, me.getUsername());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> myAllocations(HttpServletRequest httpRequest) {
        MeResponse me = authClient.me(httpRequest);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (me.getRoles() == null || !me.getRoles().contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can view their allocations");
        }
        List<Allocation> list = allocationService.getAllocationsForAdmin(me.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/student/me")
    public ResponseEntity<?> allocationsForCurrentStudent(HttpServletRequest httpRequest) {
        MeResponse me = authClient.me(httpRequest);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only students can view their allocations");
        }
        List<Allocation> list = allocationService.getAllocationsForStudent(me.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/api/check-student")
    public ResponseEntity<?> checkStudent(@RequestParam("username") String username, HttpServletRequest httpRequest) {
        MeResponse me = authClient.me(httpRequest);
        if (me == null || !me.isAuthenticated() || me.getRoles() == null || !me.getRoles().contains("admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> out = new HashMap<>();
        out.put("query", username);
        if (username == null || username.isBlank()) {
            out.put("exists", false);
            return ResponseEntity.ok(out);
        }
        UserLookupResponse user = authClient.findUserByUsername(username.trim());
        boolean ok = user != null && user.isExists() && user.getRoles() != null && user.getRoles().contains("student");
        out.put("exists", ok);
        return ResponseEntity.ok(out);
    }
}
