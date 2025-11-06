package com.example.job_service.controller;

import com.example.job_service.dto.JobRequest;
import com.example.job_service.dto.JobResponse;
import com.example.job_service.model.Job;
import com.example.job_service.service.JobService;
import com.example.job_service.auth.AuthServiceClient;
import com.example.job_service.auth.MeResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private JobResponse mapToResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getType(),
                job.getLocation()
        );
    }

    private final JobService jobService;
    private final AuthServiceClient authClient;

    @Autowired
    public JobController(JobService jobService, AuthServiceClient authClient) {
        this.jobService = jobService;
        this.authClient = authClient;
    }

    @GetMapping
    public ResponseEntity<?> getAllJobs(@RequestParam(required = false) String alumniUsername,
                                        HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Job> jobs;
        boolean isAlumni = me.getRoles() != null && me.getRoles().contains("alumni");
        if (alumniUsername != null && !alumniUsername.isBlank()) {
            jobs = jobService.searchJobsByAlumniUsername(alumniUsername.trim());
        } else if (isAlumni) {
            jobs = jobService.getJobsByAlumniUsername(me.getUsername());
        } else {
            jobs = jobService.getAllJobs();
        }
        return ResponseEntity.ok(jobs.stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id, HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Job job = jobService.getJobById(id);
        if (job == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapToResponse(job));
    }

    // Public existence check for inter-service calls
    @GetMapping("/{id}/exists")
    public ResponseEntity<?> jobExists(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        if (job == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody JobRequest jobRequest, HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (me.getRoles() == null || !me.getRoles().contains("alumni")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only alumni can create jobs");
        }
        // Let service assign sequential id
        Job job = new Job(null, me.getUsername(),
                jobRequest.getTitle(),
                jobRequest.getDescription(),
                jobRequest.getType(),
                jobRequest.getLocation());
        Job savedJob = jobService.createJob(job, me.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedJob));
    }
}
