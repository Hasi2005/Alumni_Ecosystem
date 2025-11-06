package com.example.job_service.dto;

import com.example.job_service.model.Outcome;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class ReferralResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long jobId;
    private String studentUsername;
    private String alumniUsername;
    private Outcome status;

    public ReferralResponse(Long id, Long jobId, String studentUsername, String alumniUsername, Outcome status) {
        this.id = id;
        this.jobId = jobId;
        this.studentUsername = studentUsername;
        this.alumniUsername = alumniUsername;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getJobId() { return jobId; }
    public String getStudentUsername() { return studentUsername; }
    public String getAlumniUsername() { return alumniUsername; }
    public Outcome getStatus() { return status; }
}
