package com.placementtracker.repository;

import com.placementtracker.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByDifficultyLevel(String difficultyLevel);
    List<Problem> findByPlatform(String platform);
    Optional<Problem> findByTitleAndPlatform(String title, String platform);
}