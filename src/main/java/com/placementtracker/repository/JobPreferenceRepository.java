package com.placementtracker.repository;

import com.placementtracker.model.JobPreference;
import com.placementtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPreferenceRepository extends JpaRepository<JobPreference, Long> {
    Optional<JobPreference> findByUser(User user);
    Optional<JobPreference> findByUserId(Long userId);
    boolean existsByUser(User user);
}