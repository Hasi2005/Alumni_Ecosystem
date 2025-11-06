package com.example.job_service.repository;

import com.example.job_service.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JPA repository for Referral
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByStudentUsername(String studentUsername);
    List<Referral> findByAlumniUsername(String alumniUsername);
}
