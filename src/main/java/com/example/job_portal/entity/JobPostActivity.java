package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "job_post_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_post_id")
    private Integer jobPostId;
    
    @ManyToOne
    @JoinColumn(name = "posted_by_id", referencedColumnName = "user_account_id")
    private RecruiterProfile postedById;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_location_id", referencedColumnName = "id")
    private JobLocation jobLocationId;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_company_id", referencedColumnName = "id")
    private JobCompany jobCompanyId;
    
    @Column(name = "job_type", length = 255)
    private String jobType;
    
    @Column(name = "job_title", length = 255)
    private String jobTitle;
    
    @Column(name = "description_of_job", length = 10000)
    private String descriptionOfJob;
    
    @Column(name = "salary", length = 255)
    private String salary;
    
    @Column(name = "remote", length = 255)
    private String remote;
    
    @Column(name = "posted_date")
    private LocalDate postedDate;
}
