package com.example.job_service.service;

import com.example.job_service.model.Application;
import com.example.job_service.model.Outcome;
import com.example.job_service.repository.ApplicationRepository;
import com.example.job_service.dto.ApplicationRequest;
import com.example.job_service.dto.ApplicationResponse;
import com.example.job_service.repository.JobRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    // Get all applications (used internally if needed)
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get applications for a specific student
    public List<ApplicationResponse> getApplicationsByStudentUsername(String studentUsername) {
        return applicationRepository.findByStudentUsername(studentUsername).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Apply for a job (student)
    public ApplicationResponse applyForJob(ApplicationRequest request, String studentUsername) {
        if (request == null || request.getJobId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jobId is required");
        }
        Long jobId = request.getJobId();
        if (!jobRepository.existsById(jobId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        // Let the database auto-generate the application ID
        Application application = new Application(
                null,
                jobId,
                studentUsername,
                Outcome.APPLIED
        );

        Application savedApp = applicationRepository.save(application);
        return mapToResponse(savedApp);
    }

    // Update application status (admin)
    public ApplicationResponse updateStatus(Long id, Outcome status) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus(status);  // make sure Application entity has setStatus()
        Application updatedApp = applicationRepository.save(app);
        return mapToResponse(updatedApp);
    }

    // Mapper from Application model → ApplicationResponse DTO
    private ApplicationResponse mapToResponse(Application app) {
        return new ApplicationResponse(
                app.getId(),
                app.getJobId(),
                app.getStudentUsername(),
                app.getStatus()
        );
    }
}
