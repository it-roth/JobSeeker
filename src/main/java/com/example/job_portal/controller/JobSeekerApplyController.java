package com.example.job_portal.controller;

import com.example.job_portal.entity.JobPostActivity;
import com.example.job_portal.entity.JobSeekerApply;
import com.example.job_portal.entity.JobSeekerProfile;
import com.example.job_portal.entity.Users;
import com.example.job_portal.service.JobPostActivityService;
import com.example.job_portal.service.JobSeekerApplyService;
import com.example.job_portal.service.JobSeekerProfileService;
import com.example.job_portal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/job-apply")
public class JobSeekerApplyController {
    
    @Autowired
    private JobSeekerApplyService jobSeekerApplyService;
    
    @Autowired
    private JobPostActivityService jobPostActivityService;
    
    @Autowired
    private JobSeekerProfileService jobSeekerProfileService;
    
    @Autowired
    private UsersService usersService;
    
    @PostMapping("/{jobId}")
    public String applyForJob(@PathVariable Integer jobId, 
                              @RequestParam(required = false) String coverLetter,
                              Authentication authentication) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getJobSeekerProfileById(user.get().getUserId());
            Optional<JobPostActivity> jobPost = jobPostActivityService.getJobPostById(jobId);
            
            if (jobSeekerProfile.isPresent() && jobPost.isPresent()) {
                JobSeekerApply application = new JobSeekerApply();
                application.setUserId(jobSeekerProfile.get());
                application.setJob(jobPost.get());
                application.setCoverLetter(coverLetter);
                jobSeekerApplyService.applyForJob(application);
            }
        }
        
        return "redirect:/job-post/" + jobId + "?applied=true";
    }
    
    @GetMapping("/my-applications")
    public String getMyApplications(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            List<JobSeekerApply> applications = jobSeekerApplyService.getApplicationsByJobSeekerId(user.get().getUserId());
            model.addAttribute("applications", applications);
        }
        
        return "my-applications";
    }
}
