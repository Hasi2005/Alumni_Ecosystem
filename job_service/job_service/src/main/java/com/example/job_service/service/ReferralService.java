package com.example.job_service.service;

import com.example.job_service.model.Referral;
import com.example.job_service.model.Outcome;
import com.example.job_service.repository.ReferralRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class ReferralService {

    @Autowired
    private ReferralRepository referralRepository;

    // Student: Request a referral
    public Referral requestReferral(Long jobId, String studentUsername, String alumniUsername) {
        Referral referral = new Referral(
                null,
                jobId,
                studentUsername,
                alumniUsername,
                Outcome.APPLIED
        );
        return referralRepository.save(referral);
    }

    // Student: View all their referral requests
    public List<Referral> getReferralsByStudent(String studentUsername) {
        return referralRepository.findByStudentUsername(studentUsername);
    }

    // Alumni: View referrals they received from students
    public List<Referral> getReferralsForAlumni(String alumniUsername) {
        return referralRepository.findByAlumniUsername(alumniUsername);
    }

    // Alumni: Update referral status (e.g., ACCEPTED / REJECTED / APPLIED)
    public Referral updateReferralStatus(Long referralId, Outcome status) {
        Optional<Referral> optionalReferral = referralRepository.findById(referralId);
        if (optionalReferral.isPresent()) {
            Referral referral = optionalReferral.get();
            referral.setStatus(status);
            return referralRepository.save(referral);
        }
        return null;
    }

    // Optional: For Admin/debugging - view all referrals
    public List<Referral> getAllReferrals() {
        return referralRepository.findAll();
    }

    // Optional: Find referral by ID
    public Optional<Referral> getReferralById(Long id) {
        return referralRepository.findById(id);
    }
}
