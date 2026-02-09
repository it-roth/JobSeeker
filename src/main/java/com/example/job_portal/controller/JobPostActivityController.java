package com.example.job_portal.controller;

import com.example.job_portal.entity.JobPostActivity;
import com.example.job_portal.service.JobPostActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/jobs")
public class JobPostActivityController {
    
    @Autowired
    private JobPostActivityService jobPostActivityService;
    
    @GetMapping("/all")
    public String getAllJobPosts(Model model) {
        List<JobPostActivity> jobPosts = jobPostActivityService.getAllJobPosts();
        model.addAttribute("jobPosts", jobPosts);
        return "job-posts";
    }
    
    @GetMapping("/{id}")
    public String viewJobPost(@PathVariable Integer id, Model model) {
        Optional<JobPostActivity> jobPost = jobPostActivityService.getJobPostById(id);
        jobPost.ifPresent(post -> model.addAttribute("jobPost", post));
        return "job-detail";
    }
}
