package com.placementtracker.service;

import com.placementtracker.dto.DiscussionCommentDto;
import com.placementtracker.dto.GroupDiscussionDto;
import com.placementtracker.model.DiscussionComment;
import com.placementtracker.model.GroupDiscussion;
import com.placementtracker.model.StudyGroup;
import com.placementtracker.model.User;
import com.placementtracker.repository.DiscussionCommentRepository;
import com.placementtracker.repository.GroupDiscussionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupDiscussionServiceImpl implements GroupDiscussionService {

    private final GroupDiscussionRepository discussionRepository;
    private final DiscussionCommentRepository commentRepository;

    @Override
    @Transactional
    public GroupDiscussion createDiscussion(User user, StudyGroup group, GroupDiscussionDto discussionDto) {
        GroupDiscussion discussion = new GroupDiscussion();
        discussion.setGroup(group);
        discussion.setUser(user);
        discussion.setTitle(discussionDto.getTitle());
        discussion.setContent(discussionDto.getContent());
        discussion.setProblemId(discussionDto.getProblemId());
        
        return discussionRepository.save(discussion);
    }

    @Override
    public List<GroupDiscussion> getDiscussionsByGroup(StudyGroup group) {
        return discussionRepository.findByGroupOrderByCreatedAtDesc(group);
    }

    @Override
    public Optional<GroupDiscussion> getDiscussionById(Long id) {
        return discussionRepository.findById(id);
    }

    @Override
    public List<GroupDiscussion> getDiscussionsByGroupAndProblem(StudyGroup group, Long problemId) {
        return discussionRepository.findByGroupAndProblemIdOrderByCreatedAtDesc(group, problemId);
    }

    @Override
    @Transactional
    public DiscussionComment addComment(User user, GroupDiscussion discussion, DiscussionCommentDto commentDto) {
        DiscussionComment comment = new DiscussionComment();
        comment.setUser(user);
        comment.setDiscussion(discussion);
        comment.setContent(commentDto.getContent());
        
        return commentRepository.save(comment);
    }

    @Override
    public List<DiscussionComment> getCommentsByDiscussion(GroupDiscussion discussion) {
        return commentRepository.findByDiscussionOrderByCreatedAtAsc(discussion);
    }

    @Override
    @Transactional
    public boolean deleteDiscussion(GroupDiscussion discussion) {
        // First delete all comments
        List<DiscussionComment> comments = commentRepository.findByDiscussionOrderByCreatedAtAsc(discussion);
        commentRepository.deleteAll(comments);
        
        // Then delete the discussion
        discussionRepository.delete(discussion);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteComment(DiscussionComment comment) {
        commentRepository.delete(comment);
        return true;
    }
}