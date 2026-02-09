package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skills {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", length = 255)
    private String name;
    
    @Column(name = "experience_level", length = 255)
    private String experienceLevel;
    
    @Column(name = "years_of_experience", length = 255)
    private String yearsOfExperience;
    
    @ManyToOne
    @JoinColumn(name = "job_seeker_profile", referencedColumnName = "user_account_id")
    private JobSeekerProfile jobSeekerProfile;
}
