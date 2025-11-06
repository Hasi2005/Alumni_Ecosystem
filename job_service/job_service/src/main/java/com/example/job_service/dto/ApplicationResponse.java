package com.example.job_service.dto;

import com.example.job_service.model.Outcome;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class ApplicationResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long jobId;
    private String studentUsername;
    private Outcome status;

    public ApplicationResponse(Long id, Long jobId, String studentUsername, Outcome status) {
        this.id = id;
        this.jobId = jobId;
        this.studentUsername = studentUsername;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getJobId() { return jobId; }
    public String getStudentUsername() { return studentUsername; }
    public Outcome getStatus() { return status; }
}
