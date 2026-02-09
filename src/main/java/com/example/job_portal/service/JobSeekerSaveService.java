package com.example.job_portal.service;

import com.example.job_portal.entity.JobSeekerSave;
import com.example.job_portal.repository.JobSeekerSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerSaveService {
    
    @Autowired
    private JobSeekerSaveRepository jobSeekerSaveRepository;
    
    public JobSeekerSave saveJob(JobSeekerSave jobSeekerSave) {
        return jobSeekerSaveRepository.save(jobSeekerSave);
    }
    
    public List<JobSeekerSave> getSavedJobsByJobSeekerId(Integer userId) {
        return jobSeekerSaveRepository.findByUserId_UserAccountId(userId);
    }
    
    public void deleteSavedJob(Integer id) {
        jobSeekerSaveRepository.deleteById(id);
    }
}
