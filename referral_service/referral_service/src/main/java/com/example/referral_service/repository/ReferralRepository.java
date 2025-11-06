package com.example.referral_service.repository;

import com.example.referral_service.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByStudentUsername(String studentUsername);
    List<Referral> findByAlumniUsername(String alumniUsername);
}

