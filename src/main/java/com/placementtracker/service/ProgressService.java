package com.placementtracker.service;

import com.placementtracker.dto.UserProgressDto;
import com.placementtracker.model.User;
import com.placementtracker.model.UserProgress;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProgressService {
    UserProgress recordProgress(User user, UserProgressDto progressDto);
    List<UserProgress> getUserProgress(User user);
    Optional<UserProgress> getUserProgressById(Long id);
    long getTotalProblemsSolved(User user);
    long getProblemsSolvedByDifficulty(User user, String difficultyLevel);
    long getCurrentStreak(User user);
    Map<String, Long> getProblemsByPlatform(User user);
    Map<String, Long> getProblemsByDifficulty(User user);
}