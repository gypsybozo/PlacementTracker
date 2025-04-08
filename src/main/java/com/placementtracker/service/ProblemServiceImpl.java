package com.placementtracker.service;

import com.placementtracker.dto.ProblemDto;
import com.placementtracker.model.Problem;
import com.placementtracker.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public Problem createProblem(ProblemDto problemDto) {
        Problem problem = new Problem();
        BeanUtils.copyProperties(problemDto, problem);
        return problemRepository.save(problem);
    }

    @Override
    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    @Override
    public Optional<Problem> getProblemById(Long id) {
        return problemRepository.findById(id);
    }

    @Override
    public List<Problem> getProblemsByDifficulty(String difficultyLevel) {
        return problemRepository.findByDifficultyLevel(difficultyLevel);
    }

    @Override
    public List<Problem> getProblemsByPlatform(String platform) {
        return problemRepository.findByPlatform(platform);
    }

    @Override
    @Transactional
    public Optional<Problem> findOrCreateProblem(ProblemDto problemDto) {
        // Try to find existing problem with the same title and platform
        Optional<Problem> existingProblem = problemRepository.findByTitleAndPlatform(
                problemDto.getTitle(), problemDto.getPlatform());
        
        if (existingProblem.isPresent()) {
            return existingProblem;
        }
        
        // Create new problem if not found
        Problem newProblem = new Problem();
        BeanUtils.copyProperties(problemDto, newProblem);
        return Optional.of(problemRepository.save(newProblem));
    }
}