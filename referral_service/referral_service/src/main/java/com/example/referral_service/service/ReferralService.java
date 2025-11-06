package com.example.referral_service.service;

import com.example.referral_service.model.Referral;
import com.example.referral_service.model.Outcome;
import com.example.referral_service.repository.ReferralRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import com.example.referral_service.job.JobServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReferralService {

    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    private JobServiceClient jobServiceClient;

    public Referral requestReferral(Long jobId, String studentUsername, String alumniUsername) {
        if (jobId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jobId is required");
        }
        if (!jobServiceClient.jobExists(jobId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        Referral referral = new Referral(
                null,
                jobId,
                studentUsername,
                alumniUsername,
                Outcome.APPLIED
        );
        return referralRepository.save(referral);
    }

    public List<Referral> getReferralsByStudent(String studentUsername) {
        return referralRepository.findByStudentUsername(studentUsername);
    }

    public List<Referral> getReferralsForAlumni(String alumniUsername) {
        return referralRepository.findByAlumniUsername(alumniUsername);
    }

    public Referral updateReferralStatus(Long referralId, Outcome status) {
        Optional<Referral> optionalReferral = referralRepository.findById(referralId);
        if (optionalReferral.isPresent()) {
            Referral referral = optionalReferral.get();
            referral.setStatus(status);
            return referralRepository.save(referral);
        }
        return null;
    }

    public List<Referral> getAllReferrals() {
        return referralRepository.findAll();
    }

    public Optional<Referral> getReferralById(Long id) {
        return referralRepository.findById(id);
    }
}
