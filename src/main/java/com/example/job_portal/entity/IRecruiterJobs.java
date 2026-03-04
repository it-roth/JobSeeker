package com.example.job_portal.entity;

public interface IRecruiterJobs {
    Long getTotalCandidates();
    Integer getJob_post_id();
    String getJob_title();
    Integer getLocationId();
    String getCity();
    String getState();
    String getCountry();
    Integer getCompanyId();
    String getName();
}
