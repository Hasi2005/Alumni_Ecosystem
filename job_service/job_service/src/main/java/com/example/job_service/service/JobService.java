package com.example.job_service.service;

import com.example.job_service.model.Job;
import com.example.job_service.repository.JobRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobIdGenerator jobIdGenerator;

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    public Job createJob(Job job, String alumniUsername) {
        // Assign a sequential ID explicitly
        Long id = job.getId();
        if (id == null || id <= 0) {
            id = jobIdGenerator.nextId();
        }
        Job newJob = new Job(id, alumniUsername, job.getTitle(), job.getDescription(), job.getType(), job.getLocation());
        return jobRepository.save(newJob);
    }

    public List<Job> getJobsByAlumniUsername(String alumniUsername) {
        return jobRepository.findByAlumniUsername(alumniUsername);
    }

    public List<Job> searchJobsByAlumniUsername(String alumniUsernameLike) {
        return jobRepository.findByAlumniUsernameContainingIgnoreCase(alumniUsernameLike);
    }
}
