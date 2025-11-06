package com.example.job_service.dto;

public class JobRequest {
    private String title;
    private String description;
    private String type;     // JOB or INTERNSHIP
    private String location;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
