package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_type_id", referencedColumnName = "user_type_id")
    private UsersType userTypeId;
}
