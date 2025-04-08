package com.placementtracker.repository;

import com.placementtracker.model.StudyGroup;
import com.placementtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    List<StudyGroup> findByCreator(User creator);
    
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.creator = ?1 OR ?1 MEMBER OF sg.members")
    List<StudyGroup> findByUserMembership(User user);
    
    Optional<StudyGroup> findByInvitationCode(String invitationCode);
}