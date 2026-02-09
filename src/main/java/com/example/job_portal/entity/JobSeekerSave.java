package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_seeker_save")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerSave {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_account_id")
    private JobSeekerProfile userId;
    
    @ManyToOne
    @JoinColumn(name = "job", referencedColumnName = "job_post_id")
    private JobPostActivity job;
}
