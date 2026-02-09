package com.example.job_portal.controller;

import com.example.job_portal.entity.JobLocation;
import com.example.job_portal.service.JobLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/job-location")
public class JobLocationController {
    
    @Autowired
    private JobLocationService jobLocationService;
    
    @GetMapping("/all")
    public String getAllJobLocations(Model model) {
        List<JobLocation> jobLocations = jobLocationService.getAllJobLocations();
        model.addAttribute("jobLocations", jobLocations);
        return "job-locations";
    }
    
    @PostMapping("/add")
    public String addJobLocation(@ModelAttribute JobLocation jobLocation) {
        jobLocationService.saveJobLocation(jobLocation);
        return "redirect:/job-location/all";
    }
}
