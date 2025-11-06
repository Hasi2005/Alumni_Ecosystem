package com.example.fund_allocation2.repository;


import com.example.fund_allocation2.models.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByAlumniUsername(String alumniUsername);
}
