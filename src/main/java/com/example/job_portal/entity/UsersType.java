package com.example.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_type_id")
    private Integer userTypeId;
    
    @Column(name = "user_type_name", nullable = false, length = 255)
    private String userTypeName;
    
    @OneToMany(mappedBy = "userTypeId", cascade = CascadeType.ALL)
    private List<Users> users;
}
