package com.example.fund_allocation2.models;

import jakarta.persistence.*;

@Entity
public class Donation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;

    // Replace relation to local User with username string from auth-service
    private String alumniUsername;

    public Donation() {}

    public Long getId() { return id; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getAlumniUsername() { return alumniUsername; }
    public void setAlumniUsername(String alumniUsername) { this.alumniUsername = alumniUsername; }
}
