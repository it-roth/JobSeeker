package com.example.job_portal.service;

import com.example.job_portal.entity.Users;
import com.example.job_portal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
    // Normalize the user type name to PascalCase (e.g. "Job Seeker" -> "JobSeeker")
    String rawUserType = user.getUserTypeId() != null ? user.getUserTypeId().getUserTypeName() : "User";
    String[] parts = rawUserType.trim().split("\\s+");
    StringBuilder sb = new StringBuilder();
    for (String p : parts) {
        if (p.length() == 0) continue;
        sb.append(Character.toUpperCase(p.charAt(0)));
        if (p.length() > 1) sb.append(p.substring(1));
    }
    String normalizedType = sb.toString();
    String authority = "ROLE_" + normalizedType;

    // Log the authority assignment
    log.debug("Assigning authority '{}' for user: {} (raw type='{}')", authority, email, rawUserType);

    return new User(
        user.getEmail(),
        user.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority(authority))
    );
    }
}
