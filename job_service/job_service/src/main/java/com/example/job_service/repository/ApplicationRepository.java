package com.example.job_service.repository;

import com.example.job_service.model.Application;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentUsername(String studentUsername);
}
