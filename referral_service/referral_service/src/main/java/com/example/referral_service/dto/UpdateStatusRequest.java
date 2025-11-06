package com.example.referral_service.dto;

import com.example.referral_service.model.Outcome;

public class UpdateStatusRequest {
    private Outcome status;

    public Outcome getStatus() { return status; }
    public void setStatus(Outcome status) { this.status = status; }
}

