package com.example.fund_allocation2.models;

import jakarta.persistence.*;

@Entity
public class Allocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private String purpose;

    // Store usernames from auth-service instead of local User relations
    private String studentUsername;
    private String adminUsername;

    public Allocation() {}

    public Long getId() { return id; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getStudentUsername() { return studentUsername; }
    public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }
    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }
}
