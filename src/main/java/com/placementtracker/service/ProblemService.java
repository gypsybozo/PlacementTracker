package com.placementtracker.service;

import com.placementtracker.dto.ProblemDto;
import com.placementtracker.model.Problem;

import java.util.List;
import java.util.Optional;

public interface ProblemService {
    Problem createProblem(ProblemDto problemDto);
    List<Problem> getAllProblems();
    Optional<Problem> getProblemById(Long id);
    List<Problem> getProblemsByDifficulty(String difficultyLevel);
    List<Problem> getProblemsByPlatform(String platform);
    Optional<Problem> findOrCreateProblem(ProblemDto problemDto);
}