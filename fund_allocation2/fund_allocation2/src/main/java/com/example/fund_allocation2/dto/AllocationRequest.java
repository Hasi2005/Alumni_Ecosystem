package com.example.fund_allocation2.dto;

public class AllocationRequest {
    private Double amount;
    private String purpose;
    private String studentUsername;

    public AllocationRequest() {}

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getStudentUsername() { return studentUsername; }
    public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }
}
