package com.example.job_portal.repository;

import com.example.job_portal.entity.JobLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLocationRepository extends JpaRepository<JobLocation, Integer> {
}
