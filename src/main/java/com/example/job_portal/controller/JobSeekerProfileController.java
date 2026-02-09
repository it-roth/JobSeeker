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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/jobseeker")
public class JobSeekerProfileController {
    private static final Logger log = LoggerFactory.getLogger(JobSeekerProfileController.class);
    
    @Autowired
    private JobSeekerProfileService jobSeekerProfileService;
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private JobPostActivityService jobPostActivityService;
    
    @Autowired
    private JobSeekerApplyService jobSeekerApplyService;
    
    @GetMapping("/dashboard")
    public String jobSeekerDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            // expose the user's email from DB to the view
            model.addAttribute("userEmail", user.get().getEmail());

            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getJobSeekerProfileById(user.get().getUserId());
            
            if (jobSeekerProfile.isPresent()) {
                model.addAttribute("profile", jobSeekerProfile.get());
                model.addAttribute("appliedJobs", jobSeekerApplyService.getApplicationsByJobSeekerId(jobSeekerProfile.get().getUserAccountId()));
                model.addAttribute("allJobs", jobPostActivityService.getAllJobPosts());
                model.addAttribute("totalApplications", jobSeekerApplyService.getApplicationsByJobSeekerId(jobSeekerProfile.get().getUserAccountId()).size());
            } else {
                // No profile yet, create empty profile and show dashboard anyway
                JobSeekerProfile emptyProfile = new JobSeekerProfile();
                emptyProfile.setFirstName("Guest");
                model.addAttribute("profile", emptyProfile);
                model.addAttribute("appliedJobs", List.of());
                model.addAttribute("allJobs", jobPostActivityService.getAllJobPosts());
                model.addAttribute("totalApplications", 0);
            }
        }
        
        return "jobseeker-dashboard";
    }
    
    @GetMapping("/profile")
    public String showJobSeekerProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getJobSeekerProfileById(user.get().getUserId());
            model.addAttribute("jobSeekerProfile", jobSeekerProfile.orElse(new JobSeekerProfile()));
        }
        
        return "jobseeker-profile";
    }
    
    @PostMapping("/profile")
    public String saveJobSeekerProfile(
            @ModelAttribute JobSeekerProfile jobSeekerProfile,
            @RequestParam(value = "profilePhotoFile", required = false) MultipartFile profilePhotoFile,
            @RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile,
            Authentication authentication) {
        
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            // Get existing profile if it exists
            Optional<JobSeekerProfile> existingProfileOpt = jobSeekerProfileService.getJobSeekerProfileById(user.get().getUserId());
            
            JobSeekerProfile profileToSave;
            
            if (existingProfileOpt.isPresent()) {
                // Update existing profile
                profileToSave = existingProfileOpt.get();
                profileToSave.setFirstName(jobSeekerProfile.getFirstName());
                profileToSave.setLastName(jobSeekerProfile.getLastName());
                profileToSave.setCity(jobSeekerProfile.getCity());
                profileToSave.setState(jobSeekerProfile.getState());
                profileToSave.setCountry(jobSeekerProfile.getCountry());
                profileToSave.setWorkAuthorization(jobSeekerProfile.getWorkAuthorization());
                profileToSave.setEmploymentType(jobSeekerProfile.getEmploymentType());
            } else {
                // Create new profile
                profileToSave = jobSeekerProfile;
                profileToSave.setUserId(user.get());
            }

            // Log incoming bound skills for debugging
            if (jobSeekerProfile.getSkills() != null) {
                log.debug("Incoming bound skills count: {}", jobSeekerProfile.getSkills().size());
                jobSeekerProfile.getSkills().forEach(s -> log.debug("Bound skill: id={} name='{}' years='{}' level='{}'", s.getId(), s.getName(), s.getYearsOfExperience(), s.getExperienceLevel()));
            } else {
                log.debug("No skills bound from form (jobSeekerProfile.getSkills() == null)");
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
            // If no new file and it's a new profile, keep the form value (which might be null)
            
            // Handle resume upload
            if (resumeFile != null && !resumeFile.isEmpty()) {
                try {
                    String fileName = saveFile(resumeFile, "resumes");
                    profileToSave.setResume(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            // Handle skills - clear existing and add new ones
            if (profileToSave.getSkills() != null) {
                profileToSave.getSkills().clear();
            }
            
            if (jobSeekerProfile.getSkills() != null) {
                for (var skill : jobSeekerProfile.getSkills()) {
                    if (skill != null && skill.getName() != null && !skill.getName().isEmpty()) {
                        skill.setJobSeekerProfile(profileToSave);
                        if (profileToSave.getSkills() == null) {
                            profileToSave.setSkills(new java.util.ArrayList<>());
                        }
                        profileToSave.getSkills().add(skill);
                    }
                }
            }

            // Log final skill list that will be saved
            if (profileToSave.getSkills() != null) {
                log.debug("Final profileToSave skills count: {}", profileToSave.getSkills().size());
                profileToSave.getSkills().forEach(s -> log.debug("Saving skill: id={} name='{}' years='{}' level='{}'", s.getId(), s.getName(), s.getYearsOfExperience(), s.getExperienceLevel()));
            } else {
                log.debug("No skills will be saved (profileToSave.getSkills() == null)");
            }
            
            jobSeekerProfileService.saveJobSeekerProfile(profileToSave);
        }
        
        return "redirect:/jobseeker/dashboard";
    }
    
    /**
     * Save uploaded file to the file system
     */
    private String saveFile(MultipartFile file, String subfolder) throws IOException {
        // Create upload directory if it doesn't exist
        String uploadDir = "uploads/" + subfolder;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = Paths.get(uploadDir, uniqueFilename);
        Files.write(filePath, file.getBytes());
        
        return uniqueFilename;
    }
    
    // Job search and application routes
    @GetMapping("/jobs")
    public String searchJobs(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String location,
                            Model model) {
        List<JobPostActivity> allJobs = jobPostActivityService.getAllJobPosts();
        
        // Filter jobs if search parameters are provided
        if (search != null && !search.isEmpty()) {
            allJobs = allJobs.stream()
                .filter(job -> job.getJobTitle().toLowerCase().contains(search.toLowerCase()) ||
                              (job.getJobCompanyId() != null && job.getJobCompanyId().getName().toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());
        }
        
        if (location != null && !location.isEmpty()) {
            allJobs = allJobs.stream()
                .filter(job -> job.getJobLocationId() != null &&
                              (job.getJobLocationId().getCity().toLowerCase().contains(location.toLowerCase()) ||
                               job.getJobLocationId().getCountry().toLowerCase().contains(location.toLowerCase())))
                .collect(Collectors.toList());
        }
        
        model.addAttribute("jobPosts", allJobs);
        return "jobseeker-jobs";
    }
    
    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Integer id, Model model) {
        Optional<JobPostActivity> job = jobPostActivityService.getJobPostById(id);
        if (job.isPresent()) {
            model.addAttribute("job", job.get());
        }
        return "job-detail";
    }
    
    @PostMapping("/apply/{id}")
    public String applyForJob(@PathVariable Integer id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getJobSeekerProfileById(user.get().getUserId());
            Optional<JobPostActivity> jobPost = jobPostActivityService.getJobPostById(id);
            
            if (jobSeekerProfile.isPresent() && jobPost.isPresent()) {
                // Check if already applied
                List<JobSeekerApply> existingApplications = jobSeekerApplyService.getApplicationsByJobSeekerId(jobSeekerProfile.get().getUserAccountId());
                boolean alreadyApplied = existingApplications.stream()
                    .anyMatch(app -> app.getJob().getJobPostId().equals(id));
                
                if (!alreadyApplied) {
                    JobSeekerApply application = new JobSeekerApply();
                    application.setUserId(jobSeekerProfile.get());
                    application.setJob(jobPost.get());
                    jobSeekerApplyService.applyForJob(application);
                    return "redirect:/jobseeker/jobs/" + id + "?success";
                } else {
                    return "redirect:/jobseeker/jobs/" + id + "?error";
                }
            }
        }
        
        return "redirect:/jobseeker/jobs/" + id + "?error";
    }
    
    @GetMapping("/applications")
    public String myApplications(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getJobSeekerProfileById(user.get().getUserId());
            if (jobSeekerProfile.isPresent()) {
                model.addAttribute("applications", jobSeekerApplyService.getApplicationsByJobSeekerId(jobSeekerProfile.get().getUserAccountId()));
            }
        }
        
        return "jobseeker-applications";
    }
}
