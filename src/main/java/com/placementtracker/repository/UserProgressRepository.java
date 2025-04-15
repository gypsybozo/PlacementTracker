package com.placementtracker.repository;

import com.placementtracker.model.Problem;
import com.placementtracker.model.User;
import com.placementtracker.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    List<UserProgress> findByUser(User user);
    List<UserProgress> findByUserOrderByCompletedAtDesc(User user);
    Optional<UserProgress> findByUserAndProblem(User user, Problem problem);
    
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user = ?1")
    long countByUser(User user);
    
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user = ?1 AND up.problem.difficultyLevel = ?2")
    long countByUserAndDifficultyLevel(User user, String difficultyLevel);
    
    @Query("SELECT COUNT(DISTINCT DATE(up.completedAt)) FROM UserProgress up " +
           "WHERE up.user = ?1 AND up.completedAt BETWEEN ?2 AND ?3")
    long countDistinctActiveDaysBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    long countByUserAndCompletedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    @Query("SELECT up FROM UserProgress up WHERE up.user.id IN :userIds")
    List<UserProgress> findByUserIdIn(List<Long> userIds);
    
    @Query("SELECT up FROM UserProgress up JOIN up.problem p WHERE up.user.id = :userId")
    List<UserProgress> findByUserId(Long userId);
}