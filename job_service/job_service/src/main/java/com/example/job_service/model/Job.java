package com.example.job_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    private Long id;
    private String alumniUsername;
    private String title;
    private String description;
    private String type;     // JOB or INTERNSHIP
    private String location;

    public Job(Long id, String alumniUsername, String title, String description, String type, String location) {
        this.id = id;
        this.alumniUsername = alumniUsername;
        this.title = title;
        this.description = description;
        this.type = type;
        this.location = location;
    }

    public Job() {}  // JPA requires a no-args constructor

    // Getters & setters
    public Long getId() { return id; }
    public String getAlumniUsername() { return alumniUsername; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getLocation() { return location; }

    public void setId(Long id) { this.id = id; }
}
