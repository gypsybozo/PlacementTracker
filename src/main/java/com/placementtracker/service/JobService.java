package com.placementtracker.service;

import com.placementtracker.dto.JobDto;
import com.placementtracker.model.Job;

import java.util.List;
import java.util.Optional;

public interface JobService {
    Job createJob(JobDto jobDto);
    Job updateJob(Long id, JobDto jobDto);
    boolean deleteJob(Long id);
    Optional<Job> findById(Long id);
    List<Job> findAllJobs();
    List<Job> findActiveJobs();
    List<Job> findRecentJobs();
    List<Job> findJobsByCompanies(List<String> companies);
    List<Job> findJobsByLocations(List<String> locations);
    List<Job> findJobsMatchingUserPreferences(Long userId);
}