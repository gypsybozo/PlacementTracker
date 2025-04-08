package com.placementtracker.controller;

import com.placementtracker.dto.DiscussionCommentDto;
import com.placementtracker.dto.GroupDiscussionDto;
import com.placementtracker.model.DiscussionComment;
import com.placementtracker.model.GroupDiscussion;
import com.placementtracker.model.StudyGroup;
import com.placementtracker.model.User;
import com.placementtracker.service.GroupDiscussionService;
import com.placementtracker.service.StudyGroupService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/discussions")
public class GroupDiscussionController {

    private final GroupDiscussionService discussionService;
    private final StudyGroupService studyGroupService;
    private final UserService userService;

    @GetMapping
    public String listDiscussions(@PathVariable Long groupId,
                                  @RequestParam(required = false) Long problemId,
                                  Model model) {
        StudyGroup group = studyGroupService.getGroupById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        List<GroupDiscussion> discussions = (problemId != null)
                ? discussionService.getDiscussionsByGroupAndProblem(group, problemId)
                : discussionService.getDiscussionsByGroup(group);

        model.addAttribute("group", group);
        model.addAttribute("discussions", discussions);
        model.addAttribute("discussionDto", new GroupDiscussionDto());

        return "groups/discussions";
    }

    @PostMapping
    public String createDiscussion(@PathVariable Long groupId,
                                @Valid @ModelAttribute("discussionDto") GroupDiscussionDto discussionDto,
                                BindingResult result,
                                Authentication authentication,
                                Model model) {

        StudyGroup group = studyGroupService.getGroupById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        if (result.hasErrors()) {
            model.addAttribute("group", group);
            model.addAttribute("discussions", discussionService.getDiscussionsByGroup(group));
            return "groups/discussions";
        }

        User user = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // ✅ Actually create the discussion
        discussionService.createDiscussion(user, group, discussionDto);

        return "redirect:/groups/" + groupId + "/discussions";
    }


    @GetMapping("/{discussionId}")
    public String viewDiscussion(@PathVariable Long groupId,
                                 @PathVariable Long discussionId,
                                 Model model) {

        GroupDiscussion discussion = discussionService.getDiscussionById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discussion ID"));

        List<DiscussionComment> comments = discussionService.getCommentsByDiscussion(discussion);

        model.addAttribute("discussion", discussion);
        model.addAttribute("comments", comments);
        model.addAttribute("commentDto", new DiscussionCommentDto());

        return "groups/discussion-detail";
    }

    @PostMapping("/{discussionId}/comment")
    public String postComment(@PathVariable Long groupId,
                              @PathVariable Long discussionId,
                              @Valid @ModelAttribute("commentDto") DiscussionCommentDto commentDto,
                              BindingResult result,
                              Authentication authentication,
                              Model model) {

        GroupDiscussion discussion = discussionService.getDiscussionById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discussion ID"));

        if (result.hasErrors()) {
            List<DiscussionComment> comments = discussionService.getCommentsByDiscussion(discussion);
            model.addAttribute("discussion", discussion);
            model.addAttribute("comments", comments);
            return "groups/discussion-detail";
        }

        User user = userService.findByUsername(authentication.getName())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
        discussionService.addComment(user, discussion, commentDto);

        return "redirect:/groups/" + groupId + "/discussions/" + discussionId;
    }
}
