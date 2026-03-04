package com.example.job_portal.controller;

import com.example.job_portal.entity.JobPostActivity;
import com.example.job_portal.entity.JobSeekerProfile;
import com.example.job_portal.entity.JobSeekerSave;
import com.example.job_portal.entity.Users;
import com.example.job_portal.service.JobPostActivityService;
import com.example.job_portal.service.JobSeekerProfileService;
import com.example.job_portal.service.JobSeekerSaveService;
import com.example.job_portal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/job-save")
public class JobSeekerSaveController {
    
    @Autowired
    private JobSeekerSaveService jobSeekerSaveService;
    
    @Autowired
    private JobPostActivityService jobPostActivityService;
    
    @Autowired
    private JobSeekerProfileService jobSeekerProfileService;
    
    @Autowired
    private UsersService usersService;
    
    @PostMapping("/{jobId}")
    public String saveJob(@PathVariable Integer jobId, Authentication authentication) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getJobSeekerProfileById(user.get().getUserId());
            Optional<JobPostActivity> jobPost = jobPostActivityService.getJobPostById(jobId);
            
            if (jobSeekerProfile.isPresent() && jobPost.isPresent()) {
                JobSeekerSave savedJob = new JobSeekerSave();
                savedJob.setUserId(jobSeekerProfile.get());
                savedJob.setJob(jobPost.get());
                jobSeekerSaveService.saveJob(savedJob);
            }
        }
        
        return "redirect:/jobseeker/jobs/" + jobId + "?saved=true";
    }
    
    @GetMapping("/my-saved-jobs")
    public String getMySavedJobs(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            List<JobSeekerSave> savedJobs = jobSeekerSaveService.getSavedJobsByJobSeekerId(user.get().getUserId());
            model.addAttribute("savedJobs", savedJobs);
        }
        
        return "my-saved-jobs";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteSavedJob(@PathVariable Integer id) {
        jobSeekerSaveService.deleteSavedJob(id);
        return "redirect:/job-save/my-saved-jobs";
    }
}
