package com.placementtracker.repository;

import com.placementtracker.model.GroupDiscussion;
import com.placementtracker.model.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDiscussionRepository extends JpaRepository<GroupDiscussion, Long> {
    List<GroupDiscussion> findByGroupOrderByCreatedAtDesc(StudyGroup group);
    List<GroupDiscussion> findByGroupAndProblemIdOrderByCreatedAtDesc(StudyGroup group, Long problemId);
}