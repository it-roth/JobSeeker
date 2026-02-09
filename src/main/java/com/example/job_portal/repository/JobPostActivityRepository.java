package com.example.job_portal.repository;

import com.example.job_portal.entity.JobPostActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostActivityRepository extends JpaRepository<JobPostActivity, Integer> {
    List<JobPostActivity> findByPostedById_UserAccountId(Integer recruiterId);
}
