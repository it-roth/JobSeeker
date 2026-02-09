package com.example.job_portal.service;

import com.example.job_portal.entity.JobSeekerApply;
import com.example.job_portal.repository.JobSeekerApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobSeekerApplyService {
    
    @Autowired
    private JobSeekerApplyRepository jobSeekerApplyRepository;
    
    public JobSeekerApply applyForJob(JobSeekerApply jobSeekerApply) {
        jobSeekerApply.setApplyDate(LocalDate.now());
        return jobSeekerApplyRepository.save(jobSeekerApply);
    }
    
    public List<JobSeekerApply> getApplicationsByJobSeekerId(Integer userId) {
        return jobSeekerApplyRepository.findByUserId_UserAccountId(userId);
    }
    
    public List<JobSeekerApply> getApplicationsByJobId(Integer jobId) {
        return jobSeekerApplyRepository.findByJob_JobPostId(jobId);
    }
    
    public List<JobSeekerApply> getAllApplications() {
        return jobSeekerApplyRepository.findAll();
    }
}
