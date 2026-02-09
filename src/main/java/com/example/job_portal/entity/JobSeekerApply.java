package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "job_seeker_apply")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerApply {
    
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
    
    @Column(name = "apply_date")
    private LocalDate applyDate;
    
    @Column(name = "cover_letter", length = 255)
    private String coverLetter;
}
