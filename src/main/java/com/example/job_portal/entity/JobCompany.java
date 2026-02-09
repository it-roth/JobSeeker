package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_company")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobCompany {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "logo", length = 255)
    private String logo;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
}
