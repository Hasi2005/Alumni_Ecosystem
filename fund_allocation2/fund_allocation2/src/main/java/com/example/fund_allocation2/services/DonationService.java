package com.example.fund_allocation2.services;

import com.example.fund_allocation2.dto.DonationRequest;
import com.example.fund_allocation2.models.Donation;
import com.example.fund_allocation2.repository.DonationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService {
    private final DonationRepository donationRepo;

    public DonationService(DonationRepository donationRepo) {
        this.donationRepo = donationRepo;
    }

    public Donation donate(DonationRequest request, String alumniUsername) {
        Donation donation = new Donation();
        donation.setAmount(request.getAmount());
        donation.setAlumniUsername(alumniUsername);
        return donationRepo.save(donation);
    }

    public List<Donation> getDonationsForAlumni(String alumniUsername) {
        return donationRepo.findByAlumniUsername(alumniUsername);
    }
}
