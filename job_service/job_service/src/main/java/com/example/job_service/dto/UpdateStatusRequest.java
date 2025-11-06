package com.example.job_service.dto;

import com.example.job_service.model.Outcome;

public class UpdateStatusRequest {
    private Outcome status;

    public Outcome getStatus() { return status; }
    public void setStatus(Outcome status) { this.status = status; }
}
