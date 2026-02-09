package com.example.job_portal.service;

import com.example.job_portal.entity.JobSeekerProfile;
import com.example.job_portal.repository.JobSeekerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSeekerProfileService {
    
    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    
    public JobSeekerProfile saveJobSeekerProfile(JobSeekerProfile jobSeekerProfile) {
        return jobSeekerProfileRepository.save(jobSeekerProfile);
    }
    
    public Optional<JobSeekerProfile> getJobSeekerProfileById(Integer id) {
        return jobSeekerProfileRepository.findById(id);
    }
}
