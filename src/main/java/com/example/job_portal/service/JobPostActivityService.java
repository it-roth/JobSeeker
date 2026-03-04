package com.example.job_portal.service;

import com.example.job_portal.entity.IRecruiterJobs;
import com.example.job_portal.entity.JobCompany;
import com.example.job_portal.entity.JobLocation;
import com.example.job_portal.entity.JobPostActivity;
import com.example.job_portal.entity.JobSeekerApply;
import com.example.job_portal.entity.RecruiterJobsDto;
import com.example.job_portal.entity.RecruiterProfile;
import com.example.job_portal.repository.JobPostActivityRepository;
import com.example.job_portal.repository.JobSeekerApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobPostActivityService {
    
    @Autowired
    private JobPostActivityRepository jobPostActivityRepository;
    
    @Autowired
    private JobSeekerApplyRepository jobSeekerApplyRepository;
    
    public List<JobPostActivity> getAllJobPosts() {
        return jobPostActivityRepository.findAll();
    }
    
    public JobPostActivity saveJobPost(JobPostActivity jobPostActivity) {
        jobPostActivity.setPostedDate(LocalDate.now());
        return jobPostActivityRepository.save(jobPostActivity);
    }
    
    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }
    
    public Optional<JobPostActivity> getJobPostById(Integer id) {
        return jobPostActivityRepository.findById(id);
    }
    
    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job Not Found"));
    }
    
    public List<JobPostActivity> getJobPostsByRecruiterId(Integer recruiterId) {
        return jobPostActivityRepository.findByPostedById_UserAccountId(recruiterId);
    }
    
    public List<JobPostActivity> getJobsByRecruiter(RecruiterProfile recruiterProfile) {
        System.out.println("Service - getJobsByRecruiter called with userAccountId: " + recruiterProfile.getUserAccountId());
        List<JobPostActivity> jobs = jobPostActivityRepository.findByPostedById_UserAccountId(recruiterProfile.getUserAccountId());
        System.out.println("Service - Found " + jobs.size() + " jobs");
        return jobs;
    }
    
    public List<RecruiterJobsDto> getRecruiterJobs(int recruiter) {
        List<IRecruiterJobs> recruiterJobDtos = jobPostActivityRepository.getRecruiterJobs(recruiter);
        List<RecruiterJobsDto> recruiterJobDtosList = new ArrayList<>();
        
        for (IRecruiterJobs rec : recruiterJobDtos) {
            JobLocation loc = new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
            JobCompany comp = new JobCompany(rec.getCompanyId(), "", rec.getName());
            
            recruiterJobDtosList.add(new RecruiterJobsDto(
                rec.getTotalCandidates(), 
                rec.getJob_post_id(), 
                rec.getJob_title(), 
                loc, 
                comp
            ));
        }
        
        return recruiterJobDtosList;
    }
    
    public void deleteJobPost(Integer id) {
        // First, delete all applications for this job post
        List<JobSeekerApply> applications = jobSeekerApplyRepository.findByJob_JobPostId(id);
        if (!applications.isEmpty()) {
            jobSeekerApplyRepository.deleteAll(applications);
        }
        
        // Then delete the job post itself
        jobPostActivityRepository.deleteById(id);
    }
}
