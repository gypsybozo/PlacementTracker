package com.placementtracker.repository;

import com.placementtracker.model.DiscussionComment;
import com.placementtracker.model.GroupDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
    List<DiscussionComment> findByDiscussionOrderByCreatedAtAsc(GroupDiscussion discussion);
}