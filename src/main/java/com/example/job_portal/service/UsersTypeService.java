package com.example.job_portal.service;

import com.example.job_portal.entity.UsersType;
import com.example.job_portal.repository.UsersTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersTypeService {
    
    @Autowired
    private UsersTypeRepository usersTypeRepository;
    
    public List<UsersType> getAllUsersTypes() {
        return usersTypeRepository.findAll();
    }
}
