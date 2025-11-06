package com.example.referral_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;
    private String studentUsername;
    private String alumniUsername;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Outcome status;

    public Referral() {}

    public Referral(Long id, Long jobId, String studentUsername, String alumniUsername, Outcome status) {
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
    public void setStatus(Outcome status) { this.status = status; }
}

