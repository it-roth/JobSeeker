package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "job_seeker_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_account_id")
    private Integer userAccountId;
    
    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;
    
    @Column(name = "first_name", length = 255)
    private String firstName;
    
    @Column(name = "last_name", length = 255)
    private String lastName;
    
    @Column(name = "city", length = 255)
    private String city;
    
    @Column(name = "state", length = 255)
    private String state;
    
    @Column(name = "country", length = 255)
    private String country;
    
    @Column(name = "work_authorization", length = 255)
    private String workAuthorization;
    
    @Column(name = "employment_type", length = 255)
    private String employmentType;
    
    @Column(name = "resume", length = 255)
    private String resume;
    
    @Column(name = "profile_photo", length = 64)
    private String profilePhoto;
    
    @OneToMany(mappedBy = "jobSeekerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skills> skills;
}
