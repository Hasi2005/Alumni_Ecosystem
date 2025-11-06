package com.example.referral_service.dto;

public class ReferralRequest {
    private Long jobId;
    private String alumniUsername;

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getAlumniUsername() { return alumniUsername; }
    public void setAlumniUsername(String alumniUsername) { this.alumniUsername = alumniUsername; }
}

