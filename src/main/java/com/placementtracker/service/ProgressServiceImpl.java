package com.placementtracker.service;

import com.placementtracker.dto.ProblemDto;
import com.placementtracker.dto.UserProgressDto;
import com.placementtracker.model.Problem;
import com.placementtracker.model.User;
import com.placementtracker.model.UserProgress;
import com.placementtracker.repository.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final UserProgressRepository progressRepository;
    private final ProblemService problemService;

    @Override
    @Transactional
    public UserProgress recordProgress(User user, UserProgressDto progressDto) {
        // Create or find problem
        ProblemDto problemDto = new ProblemDto();
        problemDto.setTitle(progressDto.getProblemTitle());
        problemDto.setPlatform(progressDto.getPlatform());
        problemDto.setDifficultyLevel(progressDto.getDifficultyLevel());
        problemDto.setProblemUrl(progressDto.getProblemUrl());
        
        Optional<Problem> problemOpt = problemService.findOrCreateProblem(problemDto);
        
        if (problemOpt.isEmpty()) {
            throw new IllegalArgumentException("Failed to create or find problem");
        }
        
        Problem problem = problemOpt.get();
        
        // Check if progress already exists for this user and problem
        Optional<UserProgress> existingProgress = progressRepository.findByUserAndProblem(user, problem);
        
        UserProgress progress;
        if (existingProgress.isPresent()) {
            // Update existing progress
            progress = existingProgress.get();
        } else {
            // Create new progress entry
            progress = new UserProgress();
            progress.setUser(user);
            progress.setProblem(problem);
        }
        
        // Set/update fields
        progress.setSolution(progressDto.getSolution());
        progress.setLanguageUsed(progressDto.getLanguageUsed());
        progress.setTimeTakenMinutes(progressDto.getTimeTakenMinutes());
        progress.setNotes(progressDto.getNotes());
        progress.setCompletedAt(LocalDateTime.now());
        
        return progressRepository.save(progress);
    }

    @Override
    public List<UserProgress> getUserProgress(User user) {
        return progressRepository.findByUserOrderByCompletedAtDesc(user);
    }

    @Override
    public Optional<UserProgress> getUserProgressById(Long id) {
        return progressRepository.findById(id);
    }

    @Override
    public long getTotalProblemsSolved(User user) {
        return progressRepository.countByUser(user);
    }

    @Override
    public long getProblemsSolvedByDifficulty(User user, String difficultyLevel) {
        return progressRepository.countByUserAndDifficultyLevel(user, difficultyLevel);
    }

    @Override
    public long getCurrentStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
    
        boolean solvedToday = progressRepository.countByUserAndCompletedAtBetween(user, todayStart, tomorrowStart) > 0;
    
        if (!solvedToday) {
            LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();
            boolean solvedYesterday = progressRepository.countByUserAndCompletedAtBetween(user, yesterdayStart, todayStart) > 0;
    
            if (!solvedYesterday) {
                return 0;
            }
        }
    
        int streak = 0;
        LocalDate currentDate = solvedToday ? today : today.minusDays(1);
    
        // Safety cap to avoid infinite loop
        int maxLookbackDays = 100;
    
        while (streak < maxLookbackDays) {
            LocalDateTime start = currentDate.atStartOfDay();
            LocalDateTime end = currentDate.plusDays(1).atStartOfDay();
    
            System.out.println("Checking date: " + currentDate);
    
            boolean solved = progressRepository.countByUserAndCompletedAtBetween(user, start, end) > 0;
    
            if (!solved) {
                System.out.println("No problems solved on " + currentDate + ". Streak ends.");
                break;
            }
    
            streak++;
            currentDate = currentDate.minusDays(1);
        }
    
        return streak;
    }
    
    

    @Override
    public Map<String, Long> getProblemsByPlatform(User user) {
        List<UserProgress> progress = progressRepository.findByUser(user);
        
        return progress.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getProblem().getPlatform(),
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, Long> getProblemsByDifficulty(User user) {
        List<UserProgress> progress = progressRepository.findByUser(user);
        
        Map<String, Long> result = progress.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getProblem().getDifficultyLevel(),
                        Collectors.counting()
                ));
        
        // Ensure all difficulty levels are present in the map
        if (!result.containsKey("EASY")) {
            result.put("EASY", 0L);
        }
        if (!result.containsKey("MEDIUM")) {
            result.put("MEDIUM", 0L);
        }
        if (!result.containsKey("HARD")) {
            result.put("HARD", 0L);
        }
        
        return result;
    }
}