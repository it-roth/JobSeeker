package com.example.job_portal.repository;

import com.example.job_portal.entity.JobCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCompanyRepository extends JpaRepository<JobCompany, Integer> {
}
