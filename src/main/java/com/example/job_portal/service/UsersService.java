package com.example.job_portal.service;

import com.example.job_portal.entity.Users;
import com.example.job_portal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Users registerUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setRegistrationDate(LocalDateTime.now());
        return usersRepository.save(user);
    }
    
    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
    
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }
    
    public Optional<Users> getUserById(Integer id) {
        return usersRepository.findById(id);
    }
}
