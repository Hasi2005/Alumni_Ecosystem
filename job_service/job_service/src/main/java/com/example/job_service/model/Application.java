package com.example.job_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;
    private String studentUsername;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Outcome status;

    public Application() {}

    public Application(Long id, Long jobId, String studentUsername, Outcome status) {
        this.id = id;
        this.jobId = jobId;
        this.studentUsername = studentUsername;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getJobId() { return jobId; }
    public String getStudentUsername() { return studentUsername; }
    public Outcome getStatus() { return status; }
    public void setStatus(Outcome status) { this.status = status; }
}
