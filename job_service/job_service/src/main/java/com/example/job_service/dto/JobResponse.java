package com.example.job_service.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class JobResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    private String description;
    private String type;
    private String location;

    public JobResponse(Long id, String title, String description, String type, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.location = location;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getLocation() { return location; }
}
