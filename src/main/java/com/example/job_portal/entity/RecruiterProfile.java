package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recruiter_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterProfile {
    
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
    
    @Column(name = "company", length = 255)
    private String company;
    
    @Column(name = "profile_photo", length = 64)
    private String profilePhoto;
}
