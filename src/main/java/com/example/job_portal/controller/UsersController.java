package com.example.job_portal.controller;

import com.example.job_portal.entity.Users;
import com.example.job_portal.entity.UsersType;
import com.example.job_portal.service.UsersService;
import com.example.job_portal.service.UsersTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UsersController {
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private UsersTypeService usersTypeService;
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Users());
        List<UsersType> usersTypes = usersTypeService.getAllUsersTypes();
        model.addAttribute("usersTypes", usersTypes);
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid Users user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            List<UsersType> usersTypes = usersTypeService.getAllUsersTypes();
            model.addAttribute("usersTypes", usersTypes);
            return "register";
        }
        
        // Check if email already exists
        if (usersService.findByEmail(user.getEmail()).isPresent()) {
            List<UsersType> usersTypes = usersTypeService.getAllUsersTypes();
            model.addAttribute("usersTypes", usersTypes);
            model.addAttribute("error", "Email already exists! Please use a different email.");
            return "register";
        }
        
        usersService.registerUser(user);
        return "redirect:/login?success";
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    @GetMapping("/api/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = usersService.findByEmail(email).isPresent();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
