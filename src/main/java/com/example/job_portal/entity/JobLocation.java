package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "city", length = 255)
    private String city;
    
    @Column(name = "state", length = 255)
    private String state;
    
    @Column(name = "country", length = 255)
    private String country;
}
