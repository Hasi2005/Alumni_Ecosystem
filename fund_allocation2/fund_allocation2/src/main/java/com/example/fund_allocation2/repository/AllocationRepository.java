package com.example.fund_allocation2.repository;

import com.example.fund_allocation2.models.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    // Fetch allocations created by a specific admin (by username)
    List<Allocation> findByAdminUsername(String username);
    // Fetch allocations for a specific student id
    List<Allocation> findByStudentUsername(String studentUsername);
}
