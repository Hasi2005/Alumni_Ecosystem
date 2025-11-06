package com.example.job_service.repository;

import com.example.job_service.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Extend JpaRepository to manage Job entities
public interface JobRepository extends JpaRepository<Job, Long> {
    // Custom query to get jobs by alumni username
    List<Job> findByAlumniUsername(String alumniUsername);
    // Partial matching, case-insensitive
    List<Job> findByAlumniUsernameContainingIgnoreCase(String alumniUsername);
}
