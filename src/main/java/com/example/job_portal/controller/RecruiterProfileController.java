package com.example.job_portal.controller;

import com.example.job_portal.entity.JobCompany;
import com.example.job_portal.entity.JobLocation;
import com.example.job_portal.entity.JobPostActivity;
import com.example.job_portal.entity.RecruiterProfile;
import com.example.job_portal.entity.Users;
import com.example.job_portal.repository.JobCompanyRepository;
import com.example.job_portal.repository.JobLocationRepository;
import com.example.job_portal.service.JobPostActivityService;
import com.example.job_portal.service.JobSeekerApplyService;
import com.example.job_portal.service.RecruiterProfileService;
import com.example.job_portal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import java.util.Optional;

@Controller
@RequestMapping("/recruiter")
public class RecruiterProfileController {
    
    @Autowired
    private RecruiterProfileService recruiterProfileService;
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private JobPostActivityService jobPostActivityService;
    
    @Autowired
    private JobSeekerApplyService jobSeekerApplyService;
    
    @Autowired
    private JobLocationRepository jobLocationRepository;
    
    @Autowired
    private JobCompanyRepository jobCompanyRepository;
    
    @GetMapping("/dashboard")
    public String recruiterDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            // Add user email from DB
            model.addAttribute("userEmail", user.get().getEmail());
            
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());
            
            if (recruiterProfile.isPresent()) {
                model.addAttribute("profile", recruiterProfile.get());
                model.addAttribute("jobs", jobPostActivityService.getJobsByRecruiter(recruiterProfile.get()));
                model.addAttribute("totalJobs", jobPostActivityService.getJobsByRecruiter(recruiterProfile.get()).size());
                model.addAttribute("totalApplications", jobSeekerApplyService.getAllApplications().size());
            } else {
                // No profile yet, redirect to create one
                return "redirect:/recruiter/profile";
            }
        }
        
        return "recruiter-dashboard";
    }
    
    @GetMapping("/profile")
    public String showRecruiterProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());
            model.addAttribute("recruiterProfile", recruiterProfile.orElse(new RecruiterProfile()));
        }
        
        return "recruiter-profile";
    }
    
    @PostMapping("/profile")
    public String saveRecruiterProfile(@ModelAttribute RecruiterProfile recruiterProfile,
                                       @RequestParam(value = "profilePhotoFile", required = false) MultipartFile profilePhotoFile,
                                       Authentication authentication) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);

        if (user.isPresent()) {
            // Check existing profile
            Optional<RecruiterProfile> existingProfileOpt = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());

            RecruiterProfile profileToSave;
            if (existingProfileOpt.isPresent()) {
                // Update existing profile fields
                profileToSave = existingProfileOpt.get();
                profileToSave.setFirstName(recruiterProfile.getFirstName());
                profileToSave.setLastName(recruiterProfile.getLastName());
                profileToSave.setCity(recruiterProfile.getCity());
                profileToSave.setState(recruiterProfile.getState());
                profileToSave.setCountry(recruiterProfile.getCountry());
                profileToSave.setCompany(recruiterProfile.getCompany());
            } else {
                // New profile
                profileToSave = recruiterProfile;
                profileToSave.setUserId(user.get());
            }

            // Handle profile photo upload
            if (profilePhotoFile != null && !profilePhotoFile.isEmpty()) {
                try {
                    String fileName = saveFile(profilePhotoFile, "photos");
                    profileToSave.setProfilePhoto(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            recruiterProfileService.saveRecruiterProfile(profileToSave);
        }

        return "redirect:/recruiter/dashboard";
    }

    /**
     * Save uploaded file to the file system
     */
    private String saveFile(MultipartFile file, String subfolder) throws IOException {
        String uploadDir = "uploads/" + subfolder;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path filePath = Paths.get(uploadDir, uniqueFilename);
        Files.write(filePath, file.getBytes());

        return uniqueFilename;
    }
    
    // Job posting routes
    @GetMapping("/jobs")
    public String getMyJobs(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());
            if (recruiterProfile.isPresent()) {
                model.addAttribute("jobPosts", jobPostActivityService.getJobsByRecruiter(recruiterProfile.get()));
            }
        }
        
        return "recruiter-jobs";
    }
    
    @GetMapping("/jobs/new")
    public String showNewJobForm(Model model) {
        model.addAttribute("jobPost", new JobPostActivity());
        return "job-post-form";
    }
    
    @PostMapping("/jobs/save")
    public String saveJob(@ModelAttribute JobPostActivity jobPost,
                         @RequestParam String companyName,
                         @RequestParam String locationCity,
                         @RequestParam String locationCountry,
                         Authentication authentication) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());
            
            if (recruiterProfile.isPresent()) {
                // Create or get company
                JobCompany company = new JobCompany();
                company.setName(companyName);
                company = jobCompanyRepository.save(company);
                
                // Create or get location
                JobLocation location = new JobLocation();
                location.setCity(locationCity);
                location.setCountry(locationCountry);
                location = jobLocationRepository.save(location);
                
                // Set job post details
                jobPost.setPostedById(recruiterProfile.get());
                jobPost.setJobCompanyId(company);
                jobPost.setJobLocationId(location);
                
                jobPostActivityService.saveJobPost(jobPost);
            }
        }
        
        return "redirect:/recruiter/jobs";
    }
    
    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Integer id, Model model) {
        Optional<JobPostActivity> jobPost = jobPostActivityService.getJobPostById(id);
        if (jobPost.isPresent()) {
            model.addAttribute("job", jobPost.get());
            model.addAttribute("applications", jobSeekerApplyService.getApplicationsByJobId(id));
        }
        return "job-detail-recruiter";
    }
    
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Integer id) {
        jobPostActivityService.deleteJobPost(id);
        return "redirect:/recruiter/jobs";
    }
}
