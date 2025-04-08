package com.placementtracker.service;

import com.placementtracker.dto.DiscussionCommentDto;
import com.placementtracker.dto.GroupDiscussionDto;
import com.placementtracker.model.DiscussionComment;
import com.placementtracker.model.GroupDiscussion;
import com.placementtracker.model.StudyGroup;
import com.placementtracker.model.User;

import java.util.List;
import java.util.Optional;

public interface GroupDiscussionService {
    GroupDiscussion createDiscussion(User user, StudyGroup group, GroupDiscussionDto discussionDto);
    List<GroupDiscussion> getDiscussionsByGroup(StudyGroup group);
    Optional<GroupDiscussion> getDiscussionById(Long id);
    List<GroupDiscussion> getDiscussionsByGroupAndProblem(StudyGroup group, Long problemId);
    DiscussionComment addComment(User user, GroupDiscussion discussion, DiscussionCommentDto commentDto);
    List<DiscussionComment> getCommentsByDiscussion(GroupDiscussion discussion);
    boolean deleteDiscussion(GroupDiscussion discussion);
    boolean deleteComment(DiscussionComment comment);
}