package com.example.job_portal.service;

import com.example.job_portal.entity.RecruiterProfile;
import com.example.job_portal.repository.RecruiterProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterProfileService {
    
    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;
    
    public RecruiterProfile saveRecruiterProfile(RecruiterProfile recruiterProfile) {
        // Check if profile exists
        if (recruiterProfile.getUserAccountId() != null) {
            Optional<RecruiterProfile> existing = recruiterProfileRepository.findById(recruiterProfile.getUserAccountId());
            if (existing.isPresent()) {
                // Update existing profile
                RecruiterProfile existingProfile = existing.get();
                existingProfile.setFirstName(recruiterProfile.getFirstName());
                existingProfile.setLastName(recruiterProfile.getLastName());
                existingProfile.setCity(recruiterProfile.getCity());
                existingProfile.setState(recruiterProfile.getState());
                existingProfile.setCountry(recruiterProfile.getCountry());
                existingProfile.setCompany(recruiterProfile.getCompany());
                if (recruiterProfile.getProfilePhoto() != null) {
                    existingProfile.setProfilePhoto(recruiterProfile.getProfilePhoto());
                }
                return recruiterProfileRepository.save(existingProfile);
            }
        }
        // Insert new profile
        return recruiterProfileRepository.save(recruiterProfile);
    }
    
    public Optional<RecruiterProfile> getRecruiterProfileById(Integer id) {
        return recruiterProfileRepository.findById(id);
    }
}
