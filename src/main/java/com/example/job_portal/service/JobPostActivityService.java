package com.example.job_portal.service;

import com.example.job_portal.entity.JobPostActivity;
import com.example.job_portal.entity.RecruiterProfile;
import com.example.job_portal.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JobPostActivityService {
    
    @Autowired
    private JobPostActivityRepository jobPostActivityRepository;
    
    public List<JobPostActivity> getAllJobPosts() {
        return jobPostActivityRepository.findAll();
    }
    
    public JobPostActivity saveJobPost(JobPostActivity jobPostActivity) {
        jobPostActivity.setPostedDate(LocalDate.now());
        return jobPostActivityRepository.save(jobPostActivity);
    }
    
    public Optional<JobPostActivity> getJobPostById(Integer id) {
        return jobPostActivityRepository.findById(id);
    }
    
    public List<JobPostActivity> getJobPostsByRecruiterId(Integer recruiterId) {
        return jobPostActivityRepository.findByPostedById_UserAccountId(recruiterId);
    }
    
    public List<JobPostActivity> getJobsByRecruiter(RecruiterProfile recruiterProfile) {
        return jobPostActivityRepository.findByPostedById_UserAccountId(recruiterProfile.getUserAccountId());
    }
    
    public void deleteJobPost(Integer id) {
        jobPostActivityRepository.deleteById(id);
    }
}
