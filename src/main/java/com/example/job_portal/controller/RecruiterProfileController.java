package com.example.job_portal.controller;

import com.example.job_portal.entity.JobCompany;
import com.example.job_portal.entity.JobLocation;
import com.example.job_portal.entity.JobPostActivity;
import com.example.job_portal.entity.RecruiterProfile;
import com.example.job_portal.entity.Users;
import com.example.job_portal.repository.JobCompanyRepository;
import com.example.job_portal.repository.JobLocationRepository;
import com.example.job_portal.repository.UsersRepository;
import com.example.job_portal.service.JobPostActivityService;
import com.example.job_portal.service.JobSeekerApplyService;
import com.example.job_portal.service.RecruiterProfileService;
import com.example.job_portal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/recruiter")
public class RecruiterProfileController {
    
    @Autowired
    private RecruiterProfileService recruiterProfileService;
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private JobPostActivityService jobPostActivityService;
    
    @Autowired
    private JobSeekerApplyService jobSeekerApplyService;
    
    @Autowired
    private JobLocationRepository jobLocationRepository;
    
    @Autowired
    private JobCompanyRepository jobCompanyRepository;
    
    @GetMapping("/dashboard")
    public String recruiterDashboard(@RequestParam(defaultValue = "1") int page,
                                     Authentication authentication, Model model) {
        String email = authentication.getName();
        System.out.println("Dashboard - Loading for email: " + email);
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            System.out.println("Dashboard - Found user with ID: " + user.get().getUserId());
            // Add user email and name
            model.addAttribute("userEmail", user.get().getEmail());
            
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());
            
            if (recruiterProfile.isPresent()) {
                System.out.println("Dashboard - Found profile with userAccountId: " + recruiterProfile.get().getUserAccountId());
                model.addAttribute("profile", recruiterProfile.get());
                
                // Add profile photo to model
                if (recruiterProfile.get().getProfilePhoto() != null) {
                    model.addAttribute("profilePhoto", recruiterProfile.get().getProfilePhoto());
                }
                
                // Get all jobs for this recruiter
                var allJobs = jobPostActivityService.getJobsByRecruiter(recruiterProfile.get());
                System.out.println("Dashboard - Found " + allJobs.size() + " jobs for recruiter " + recruiterProfile.get().getUserAccountId());
                
                // Pagination logic
                int pageSize = 5;
                int totalJobs = allJobs.size();
                int totalPages = (int) Math.ceil((double) totalJobs / pageSize);
                
                // Validate page number
                if (page < 1) {
                    page = 1;
                }
                if (page > totalPages && totalPages > 0) {
                    page = totalPages;
                }
                
                // Get jobs for current page
                int startIndex = (page - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, totalJobs);
                var jobs = allJobs.subList(startIndex, endIndex);
                
                // Add application count for each job
                java.util.Map<Integer, Integer> applicationCounts = new java.util.HashMap<>();
                for (JobPostActivity job : allJobs) {
                    int count = jobSeekerApplyService.getApplicationsByJobId(job.getJobPostId()).size();
                    applicationCounts.put(job.getJobPostId(), count);
                    System.out.println("Dashboard - Job ID: " + job.getJobPostId() + ", Title: " + job.getJobTitle() + ", Applications: " + count);
                }
                
                model.addAttribute("jobs", jobs);
                model.addAttribute("applicationCounts", applicationCounts);
                model.addAttribute("totalJobs", totalJobs);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("totalApplications", jobSeekerApplyService.getAllApplications().size());
            } else {
                System.out.println("Dashboard - No profile found, creating new one");
                // Create a basic profile if it doesn't exist
                RecruiterProfile newProfile = new RecruiterProfile();
                newProfile.setUserId(user.get()); // Set this FIRST - @MapsId will use the ID from Users
                RecruiterProfile savedProfile = recruiterProfileService.saveRecruiterProfile(newProfile);
                System.out.println("Dashboard - Created profile with userAccountId: " + savedProfile.getUserAccountId());
                
                model.addAttribute("profile", savedProfile);
                
                // Get all jobs for this recruiter
                var allJobs = jobPostActivityService.getJobsByRecruiter(savedProfile);
                System.out.println("Dashboard - Found " + allJobs.size() + " jobs for new recruiter " + savedProfile.getUserAccountId());
                
                // Pagination logic
                int pageSize = 5;
                int totalJobs = allJobs.size();
                int totalPages = (int) Math.ceil((double) totalJobs / pageSize);
                
                // Validate page number
                if (page < 1) {
                    page = 1;
                }
                if (page > totalPages && totalPages > 0) {
                    page = totalPages;
                }
                
                // Get jobs for current page
                int startIndex = (page - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, totalJobs);
                var jobs = allJobs.subList(startIndex, endIndex);
                
                // Add application count for each job
                java.util.Map<Integer, Integer> applicationCounts = new java.util.HashMap<>();
                for (JobPostActivity job : allJobs) {
                    int count = jobSeekerApplyService.getApplicationsByJobId(job.getJobPostId()).size();
                    applicationCounts.put(job.getJobPostId(), count);
                }
                
                model.addAttribute("jobs", jobs);
                model.addAttribute("applicationCounts", applicationCounts);
                model.addAttribute("totalJobs", totalJobs);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("totalApplications", jobSeekerApplyService.getAllApplications().size());
            }
        }
        
        return "recruiter-dashboard";
    }
    
    @GetMapping("/profile")
    public String showRecruiterProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        Users currentUser = usersRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find " + email));
        
        Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(currentUser.getUserId());
        
        if (!recruiterProfile.isEmpty()) {
            model.addAttribute("profile", recruiterProfile.get());
            
            // Add profile photo to model
            if (recruiterProfile.get().getProfilePhoto() != null) {
                model.addAttribute("profilePhoto", recruiterProfile.get().getProfilePhoto());
            }
        } else {
            model.addAttribute("profile", new RecruiterProfile());
        }
        
        return "recruiter-profile";
    }
    
    @PostMapping("/profile")
    public String saveRecruiterProfile(@ModelAttribute RecruiterProfile recruiterProfile,
                                       @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                       Authentication authentication) throws IOException {
        String email = authentication.getName();
        Users currentUser = usersRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find " + email));
        
        // Get existing profile or create new one
        Optional<RecruiterProfile> existingProfile = recruiterProfileService.getRecruiterProfileById(currentUser.getUserId());
        
        // Set userId and userAccountId
        recruiterProfile.setUserId(currentUser);
        recruiterProfile.setUserAccountId(currentUser.getUserId());
        
        String fileName = null;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            // Save the file and get the unique filename
            fileName = saveFile(multipartFile, "photos");
            recruiterProfile.setProfilePhoto(fileName);
        } else {
            // Keep existing photo if no new file uploaded
            if (existingProfile.isPresent() && existingProfile.get().getProfilePhoto() != null) {
                recruiterProfile.setProfilePhoto(existingProfile.get().getProfilePhoto());
            }
        }
        
        recruiterProfileService.saveRecruiterProfile(recruiterProfile);
        
        return "redirect:/recruiter/dashboard?profile-updated=true";
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
    
    @GetMapping("/jobs/post")
    public String showPostJobForm(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
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
        
        return "redirect:/recruiter/dashboard?posted=true";
    }
    
    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Integer id, Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            model.addAttribute("userEmail", user.get().getEmail());
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());
            if (recruiterProfile.isPresent()) {
                model.addAttribute("profile", recruiterProfile.get());
                
                // Add profile photo to model
                if (recruiterProfile.get().getProfilePhoto() != null) {
                    model.addAttribute("profilePhoto", recruiterProfile.get().getProfilePhoto());
                }
            }
        }
        
        Optional<JobPostActivity> jobPost = jobPostActivityService.getJobPostById(id);
        if (jobPost.isPresent()) {
            model.addAttribute("job", jobPost.get());
            model.addAttribute("applications", jobSeekerApplyService.getApplicationsByJobId(id));
        }
        return "job-detail-recruiter";
    }
    
    @GetMapping("/jobs/{id}/candidates")
    public String viewCandidates(@PathVariable Integer id, Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Users> user = usersService.findByEmail(email);
        
        if (user.isPresent()) {
            model.addAttribute("userEmail", user.get().getEmail());
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getRecruiterProfileById(user.get().getUserId());
            if (recruiterProfile.isPresent()) {
                model.addAttribute("profile", recruiterProfile.get());
                
                // Add profile photo to model
                if (recruiterProfile.get().getProfilePhoto() != null) {
                    model.addAttribute("profilePhoto", recruiterProfile.get().getProfilePhoto());
                }
            }
        }
        
        Optional<JobPostActivity> jobPost = jobPostActivityService.getJobPostById(id);
        if (jobPost.isPresent()) {
            model.addAttribute("job", jobPost.get());
            model.addAttribute("applications", jobSeekerApplyService.getApplicationsByJobId(id));
        }
        return "job-candidates";
    }
    
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Integer id) {
        jobPostActivityService.deleteJobPost(id);
        return "redirect:/recruiter/jobs";
    }
}
