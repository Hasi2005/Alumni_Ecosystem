package com.example.fund_allocation2.services;
import com.example.fund_allocation2.dto.AllocationRequest;
import com.example.fund_allocation2.models.Allocation;
import com.example.fund_allocation2.repository.AllocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AllocationService {
    private final AllocationRepository allocationRepo;

    // Explicit constructor
    public AllocationService(AllocationRepository allocationRepo) {
        this.allocationRepo = allocationRepo;
    }

    public Allocation allocate(AllocationRequest request, String adminUsername) {
        Allocation allocation = new Allocation();
        allocation.setAmount(request.getAmount());
        allocation.setPurpose(request.getPurpose());
        allocation.setStudentUsername(request.getStudentUsername());
        allocation.setAdminUsername(adminUsername);
        return allocationRepo.save(allocation);
    }

    public List<Allocation> getAllocationsForAdmin(String adminUsername) {
        return allocationRepo.findByAdminUsername(adminUsername);
    }

    public List<Allocation> getAllocationsForStudent(String studentUsername) {
        return allocationRepo.findByStudentUsername(studentUsername);
    }

    public Optional<Allocation> getById(Long id) {
        return allocationRepo.findById(id);
    }
}
