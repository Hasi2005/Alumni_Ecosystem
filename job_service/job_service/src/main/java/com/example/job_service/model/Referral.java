package com.example.job_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // ✅ let DB handle auto-increment
    private Long id;

    private Long jobId;
    private String studentUsername;
    private String alumniUsername;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)  // ✅ maps to VARCHAR(20)
    private Outcome status;

    public Referral() {}  // ✅ required no-args constructor for JPA

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
