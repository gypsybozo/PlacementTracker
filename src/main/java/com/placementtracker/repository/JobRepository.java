package com.placementtracker.repository;

import com.placementtracker.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByActiveTrue();
    List<Job> findByCompanyIn(List<String> companies);
    List<Job> findByTitleContainingIgnoreCase(String keyword);
    List<Job> findByLocationIn(List<String> locations);
    
    @Query("SELECT j FROM Job j WHERE j.postedDate >= :since AND j.active = true")
    List<Job> findRecentJobs(@Param("since") LocalDateTime since);
    
    @Query("SELECT j FROM Job j WHERE j.company IN :companies OR j.location IN :locations OR j.title IN :roles")
    List<Job> findByPreferences(@Param("companies") List<String> companies,
                               @Param("locations") List<String> locations,
                               @Param("roles") List<String> roles);
}