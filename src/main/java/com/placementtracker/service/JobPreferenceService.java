package com.placementtracker.service;

import com.placementtracker.dto.JobPreferenceDto;
import com.placementtracker.model.JobPreference;
import com.placementtracker.model.User;

import java.util.Optional;

public interface JobPreferenceService {
    JobPreference createOrUpdatePreference(User user, JobPreferenceDto preferenceDto);
    Optional<JobPreference> findByUser(User user);
    Optional<JobPreferenceDto> findDtoByUser(User user);
    void toggleNotifications(User user, boolean enabled);
    boolean hasPreferences(User user);
}