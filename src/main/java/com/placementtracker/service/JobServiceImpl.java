package com.placementtracker.service;

import com.placementtracker.dto.JobDto;
import com.placementtracker.model.Job;
import com.placementtracker.model.JobPreference;
import com.placementtracker.model.User;
import com.placementtracker.repository.JobPreferenceRepository;
import com.placementtracker.repository.JobRepository;
import com.placementtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobPreferenceRepository preferenceRepository;

    @Override
    @Transactional
    public Job createJob(JobDto jobDto) {
        Job job = new Job();
        mapDtoToEntity(jobDto, job);
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    public Job updateJob(Long id, JobDto jobDto) {
        Optional<Job> existingJob = jobRepository.findById(id);
        if (existingJob.isPresent()) {
            Job job = existingJob.get();
            mapDtoToEntity(jobDto, job);
            return jobRepository.save(job);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteJob(Long id) {
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent()) {
            job.get().setActive(false);
            jobRepository.save(job.get());
            return true;
        }
        return false;
    }

    @Override
    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    @Override
    public List<Job> findAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public List<Job> findActiveJobs() {
        return jobRepository.findByActiveTrue();
    }

    @Override
    public List<Job> findRecentJobs() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return jobRepository.findRecentJobs(oneWeekAgo);
    }

    @Override
    public List<Job> findJobsByCompanies(List<String> companies) {
        return jobRepository.findByCompanyIn(companies);
    }

    @Override
    public List<Job> findJobsByLocations(List<String> locations) {
        return jobRepository.findByLocationIn(locations);
    }

    @Override
    public List<Job> findJobsMatchingUserPreferences(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Collections.emptyList();
        }
        
        Optional<JobPreference> preferenceOpt = preferenceRepository.findByUser(userOpt.get());
        if (preferenceOpt.isEmpty() || !preferenceOpt.get().isNotifyEnabled()) {
            return Collections.emptyList();
        }
        
        JobPreference preferences = preferenceOpt.get();
        
        // Convert sets to lists for the repository query
        List<String> companies = new ArrayList<>(preferences.getPreferredCompanies());
        List<String> locations = new ArrayList<>(preferences.getPreferredLocations());
        List<String> roles = new ArrayList<>(preferences.getPreferredRoles());
        
        if (companies.isEmpty() && locations.isEmpty() && roles.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Job> matchingJobs = jobRepository.findByPreferences(companies, locations, roles);
        
        // Filter by skills if preferred skills are set
        if (!preferences.getPreferredSkills().isEmpty()) {
            return matchingJobs.stream()
                .filter(job -> {
                    if (job.getSkillsRequired() == null) return false;
                    String jobSkills = job.getSkillsRequired().toLowerCase();
                    return preferences.getPreferredSkills().stream()
                        .anyMatch(skill -> jobSkills.contains(skill.toLowerCase()));
                })
                .collect(Collectors.toList());
        }
        
        return matchingJobs;
    }
    
    private void mapDtoToEntity(JobDto dto, Job entity) {
        entity.setTitle(dto.getTitle());
        entity.setCompany(dto.getCompany());
        entity.setLocation(dto.getLocation());
        entity.setDescription(dto.getDescription());
        entity.setSkillsRequired(dto.getSkillsRequired());
        entity.setExperienceYears(dto.getExperienceYears());
        
        // Only set posted date if it's a new job
        if (entity.getPostedDate() == null) {
            entity.setPostedDate(LocalDateTime.now());
        }
        
        entity.setApplicationDeadline(dto.getApplicationDeadline());
        entity.setSalaryRange(dto.getSalaryRange());
        entity.setTags(dto.getTags());
        entity.setActive(dto.isActive());
    }
}