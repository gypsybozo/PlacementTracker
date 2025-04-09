package com.placementtracker.repository;

import com.placementtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.jobPreference p " +
       "WHERE (:company IS NULL OR :company IN elements(p.preferredCompanies)) " +
       "OR (:location IS NULL OR :location IN elements(p.preferredLocations)) " +
       "OR EXISTS (SELECT tag FROM p.preferredTags tag WHERE tag IN :tags)")
    List<User> findUsersInterestedInJobCriteria(@Param("company") String company,
                                                @Param("location") String location,
                                                @Param("tags") List<String> tags);



}