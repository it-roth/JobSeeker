package com.example.job_portal.service;

import com.example.job_portal.entity.JobLocation;
import com.example.job_portal.repository.JobLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobLocationService {
    
    @Autowired
    private JobLocationRepository jobLocationRepository;
    
    public List<JobLocation> getAllJobLocations() {
        return jobLocationRepository.findAll();
    }
    
    public JobLocation saveJobLocation(JobLocation jobLocation) {
        return jobLocationRepository.save(jobLocation);
    }
}
