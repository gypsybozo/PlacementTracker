package com.placementtracker.service;

import com.placementtracker.dto.JobPreferenceDto;
import com.placementtracker.model.JobPreference;
import com.placementtracker.model.User;
import com.placementtracker.repository.JobPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobPreferenceServiceImpl implements JobPreferenceService {

    private final JobPreferenceRepository preferenceRepository;

    @Override
    @Transactional
    public JobPreference createOrUpdatePreference(User user, JobPreferenceDto preferenceDto) {
        JobPreference preference = preferenceRepository.findByUser(user)
                .orElse(new JobPreference());
                
        System.out.println("Skills received: " + preferenceDto.getPreferredSkills());

        preference.setUser(user);
        preference.setPreferredCompanies(new HashSet<>(preferenceDto.getPreferredCompanies()));
        preference.setPreferredRoles(new HashSet<>(preferenceDto.getPreferredRoles()));
        preference.setPreferredLocations(new HashSet<>(preferenceDto.getPreferredLocations()));
        preference.setPreferredSkills(new HashSet<>(preferenceDto.getPreferredSkills()));
        preference.setMinSalary(preferenceDto.getMinSalary());
        preference.setNotifyEnabled(preferenceDto.isNotifyEnabled());

        return preferenceRepository.save(preference);
    }


    @Override
    public Optional<JobPreference> findByUser(User user) {
        return preferenceRepository.findByUser(user);
    }

    @Override
    public Optional<JobPreferenceDto> findDtoByUser(User user) {
        Optional<JobPreference> preferenceOpt = preferenceRepository.findByUser(user);

        if (preferenceOpt.isEmpty()) {
            return Optional.empty();
        }

        JobPreference preference = preferenceOpt.get();
        JobPreferenceDto dto = new JobPreferenceDto();
        dto.setId(preference.getId());
        dto.setPreferredCompanies(new ArrayList<>(preference.getPreferredCompanies()));
        dto.setPreferredRoles(new ArrayList<>(preference.getPreferredRoles()));
        dto.setPreferredLocations(new ArrayList<>(preference.getPreferredLocations()));
        dto.setPreferredSkills(new ArrayList<>(preference.getPreferredSkills()));
        dto.setMinSalary(preference.getMinSalary());
        dto.setNotifyEnabled(preference.isNotifyEnabled());

        return Optional.of(dto);
    }


    @Override
    @Transactional
    public void toggleNotifications(User user, boolean enabled) {
        Optional<JobPreference> preferenceOpt = preferenceRepository.findByUser(user);
        
        if (preferenceOpt.isPresent()) {
            JobPreference preference = preferenceOpt.get();
            preference.setNotifyEnabled(enabled);
            preferenceRepository.save(preference);
        }
    }

    @Override
    public boolean hasPreferences(User user) {
        return preferenceRepository.existsByUser(user);
    }
}