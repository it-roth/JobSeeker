package com.example.job_portal.repository;

import com.example.job_portal.entity.JobSeekerApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {
    List<JobSeekerApply> findByUserId_UserAccountId(Integer userId);
    List<JobSeekerApply> findByJob_JobPostId(Integer jobId);
}
