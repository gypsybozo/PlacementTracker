package com.placementtracker.controller;

import com.placementtracker.dto.LeaderboardEntryDto;
import com.placementtracker.model.StudyGroup;
import com.placementtracker.model.User;
import com.placementtracker.service.GroupLeaderboardService;
import com.placementtracker.service.StudyGroupService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LeaderboardController {

    private final GroupLeaderboardService leaderboardService;
    private final StudyGroupService studyGroupService;
    private final UserService userService;

    @GetMapping("/groups/{id}/leaderboard")
    public String viewGroupLeaderboard(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        Optional<StudyGroup> groupOpt = studyGroupService.getGroupById(id);
        
        if (userOpt.isEmpty() || groupOpt.isEmpty()) {
            return "redirect:/groups";
        }
        
        User user = userOpt.get();
        StudyGroup group = groupOpt.get();
        
        // Check if user is a member of the group
        if (!group.isMember(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be a member to view the leaderboard");
            return "redirect:/groups";
        }
        
        List<LeaderboardEntryDto> leaderboard = leaderboardService.getGroupLeaderboard(id);
        
        model.addAttribute("group", group);
        model.addAttribute("leaderboard", leaderboard);
        model.addAttribute("currentUser", user);
        model.addAttribute("isCreator", group.getCreator().equals(user));
        
        return "groups/leaderboard";
    }
}