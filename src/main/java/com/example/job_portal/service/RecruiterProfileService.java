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
        return recruiterProfileRepository.save(recruiterProfile);
    }
    
    public Optional<RecruiterProfile> getRecruiterProfileById(Integer id) {
        return recruiterProfileRepository.findById(id);
    }
}
