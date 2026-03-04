package com.example.job_portal.controller;

import com.example.job_portal.entity.JobPostActivity;
import com.example.job_portal.entity.JobSeekerApply;
import com.example.job_portal.entity.RecruiterProfile;
import com.example.job_portal.entity.Users;
import com.example.job_portal.service.JobPostActivityService;
import com.example.job_portal.service.JobSeekerApplyService;
import com.example.job_portal.service.RecruiterProfileService;
import com.example.job_portal.service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class JobPostActivityController {
    
    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerApplyService jobSeekerApplyService;

    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, 
                                     RecruiterProfileService recruiterProfileService,
                                     JobSeekerApplyService jobSeekerApplyService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerApplyService = jobSeekerApplyService;
    }

    @GetMapping("/dashboard/add")
    public String addJobs(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity, Model model) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Current User ID: " + currentUser.getUserId());
            System.out.println("Current User Email: " + currentUser.getEmail());
            
            // Check if this is an update (job has ID) or new job
            boolean isUpdate = jobPostActivity.getJobPostId() != null;
            
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(currentUser.getUserId());
            
            if (recruiterProfile.isPresent()) {
                System.out.println("Found existing profile with userAccountId: " + recruiterProfile.get().getUserAccountId());
                jobPostActivity.setPostedById(recruiterProfile.get());
            } else {
                System.out.println("Creating new recruiter profile for user: " + currentUser.getUserId());
                // Create a basic recruiter profile if it doesn't exist
                RecruiterProfile newProfile = new RecruiterProfile();
                newProfile.setUserId(currentUser); // Set this FIRST - @MapsId will use the ID from Users
                RecruiterProfile savedProfile = recruiterProfileService.saveRecruiterProfile(newProfile);
                System.out.println("Created profile with userAccountId: " + savedProfile.getUserAccountId());
                jobPostActivity.setPostedById(savedProfile);
            }
            
            jobPostActivity.setPostedDate(java.time.LocalDate.now());
            model.addAttribute("jobPostActivity", jobPostActivity);
            JobPostActivity savedJob = jobPostActivityService.addNew(jobPostActivity);
            System.out.println("Saved job with ID: " + savedJob.getJobPostId() + " linked to recruiter: " + 
                             (savedJob.getPostedById() != null ? savedJob.getPostedById().getUserAccountId() : "NULL"));
            
            // Redirect with appropriate notification parameter
            if (isUpdate) {
                return "redirect:/recruiter/dashboard?updated=true";
            } else {
                return "redirect:/recruiter/dashboard?posted=true";
            }
        } else {
            System.out.println("ERROR: Current user is NULL");
        }
        return "redirect:/recruiter/dashboard";
    }
    
    @PostMapping("/dashboard/edit/{id}")
    public String editJob(@PathVariable int id, Model model) {
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity", jobPostActivity);
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }
    
    @PostMapping("/dashboard/deleteJob/{id}")
    public String deleteJob(@PathVariable int id) {
        jobPostActivityService.deleteJobPost(id);
        return "redirect:/recruiter/dashboard?deleted=true";
    }
    
    @GetMapping("/jobs/all")
    public String getAllJobPosts(Model model) {
        List<JobPostActivity> jobPosts = jobPostActivityService.getAllJobPosts();
        model.addAttribute("jobPosts", jobPosts);
        return "job-posts";
    }
    
    @GetMapping("/jobs/{id}")
    public String viewJobPost(@PathVariable Integer id, Authentication authentication, Model model) {
        // Add user information for recruiter
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        if (user.isPresent()) {
            Optional<RecruiterProfile> profile = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());
            if (profile.isPresent()) {
                model.addAttribute("user", profile.get());
            } else {
                // Create empty profile to avoid null errors
                RecruiterProfile emptyProfile = new RecruiterProfile();
                emptyProfile.setFirstName("User");
                model.addAttribute("user", emptyProfile);
            }
        }
        
        // Add job details
        Optional<JobPostActivity> jobPost = jobPostActivityService.getJobPostById(id);
        if (jobPost.isPresent()) {
            model.addAttribute("jobDetails", jobPost.get());
            
            // Add list of applicants for this job
            List<JobSeekerApply> applicants = jobSeekerApplyService.getApplicationsByJobId(id);
            model.addAttribute("applyList", applicants);
        }
        
        return "job-details";
    }
}
