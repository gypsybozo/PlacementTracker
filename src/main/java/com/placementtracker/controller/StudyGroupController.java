package com.placementtracker.controller;

import com.placementtracker.dto.StudyGroupDto;
import com.placementtracker.model.StudyGroup;
import com.placementtracker.model.User;
import com.placementtracker.service.StudyGroupService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
    private final UserService userService;

    @GetMapping
    public String viewGroups(Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        List<StudyGroup> groups = studyGroupService.getUserGroups(user);
        
        model.addAttribute("groups", groups);
        model.addAttribute("groupDto", new StudyGroupDto());
        
        return "groups/list";
    }

    @GetMapping("/{id}")
    public String viewGroup(@PathVariable Long id, Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        Optional<StudyGroup> groupOpt = studyGroupService.getGroupById(id);
        
        if (userOpt.isEmpty() || groupOpt.isEmpty()) {
            return "redirect:/groups";
        }
        
        User user = userOpt.get();
        StudyGroup group = groupOpt.get();
        
        // Check if user is a member of the group
        if (!group.isMember(user)) {
            return "redirect:/groups?error=Not a member of this group";
        }
        
        model.addAttribute("group", group);
        model.addAttribute("isCreator", group.getCreator().equals(user));
        
        return "groups/view";
    }

    @PostMapping("/create")
    public String createGroup(
            @Valid @ModelAttribute("groupDto") StudyGroupDto groupDto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "groups/list";
        }
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        
        try {
            StudyGroup group = studyGroupService.createGroup(user, groupDto);
            redirectAttributes.addFlashAttribute("successMessage", "Study group created successfully!");
            return "redirect:/groups/" + group.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating group: " + e.getMessage());
            return "redirect:/groups";
        }
    }

    @GetMapping("/{id}/edit")
    public String editGroup(@PathVariable Long id, Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        Optional<StudyGroup> groupOpt = studyGroupService.getGroupById(id);
        
        if (userOpt.isEmpty() || groupOpt.isEmpty()) {
            return "redirect:/groups";
        }
        
        User user = userOpt.get();
        StudyGroup group = groupOpt.get();
        
        // Only the creator can edit the group
        if (!group.getCreator().equals(user)) {
            return "redirect:/groups?error=Only the creator can edit the group";
        }
        
        StudyGroupDto groupDto = new StudyGroupDto();
        groupDto.setId(group.getId());
        groupDto.setName(group.getName());
        groupDto.setDescription(group.getDescription());
        
        model.addAttribute("groupDto", groupDto);
        model.addAttribute("group", group);
        
        return "groups/edit";
    }

    @PostMapping("/{id}/update")
    public String updateGroup(
            @PathVariable Long id,
            @Valid @ModelAttribute("groupDto") StudyGroupDto groupDto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "groups/edit";
        }
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        Optional<StudyGroup> groupOpt = studyGroupService.getGroupById(id);
        
        if (userOpt.isEmpty() || groupOpt.isEmpty()) {
            return "redirect:/groups";
        }
        
        User user = userOpt.get();
        StudyGroup group = groupOpt.get();
        
        // Only the creator can update the group
        if (!group.getCreator().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only the creator can update the group");
            return "redirect:/groups";
        }
        
        try {
            studyGroupService.updateGroup(group, groupDto);
            redirectAttributes.addFlashAttribute("successMessage", "Group updated successfully!");
            return "redirect:/groups/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating group: " + e.getMessage());
            return "redirect:/groups/" + id + "/edit";
        }
    }

    @PostMapping("/join")
    public String joinGroup(
            @RequestParam String invitationCode,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        Optional<StudyGroup> groupOpt = studyGroupService.getGroupByInvitationCode(invitationCode);
        
        if (userOpt.isEmpty() || groupOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid invitation code");
            return "redirect:/groups";
        }
        
        User user = userOpt.get();
        StudyGroup group = groupOpt.get();
        
        // Check if user is already a member
        if (group.isMember(user)) {
            redirectAttributes.addFlashAttribute("infoMessage", "You are already a member of this group");
            return "redirect:/groups/" + group.getId();
        }
        
        boolean joined = studyGroupService.addMemberToGroup(group, user);
        
        if (joined) {
            redirectAttributes.addFlashAttribute("successMessage", "Successfully joined the group!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to join the group");
        }
        
        return "redirect:/groups/" + group.getId();
    }

    @GetMapping("/{id}/leave")
    public String leaveGroup(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        Optional<StudyGroup> groupOpt = studyGroupService.getGroupById(id);
        
        if (userOpt.isEmpty() || groupOpt.isEmpty()) {
            return "redirect:/groups";
        }
        
        User user = userOpt.get();
        StudyGroup group = groupOpt.get();
        
        // Cannot leave if you're the creator
        if (group.getCreator().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "As the creator, you cannot leave the group. You may delete it instead.");
            return "redirect:/groups/" + id;
        }
        
        boolean left = studyGroupService.removeMemberFromGroup(group, user);
        
        if (left) {
            redirectAttributes.addFlashAttribute("successMessage", "You have left the group");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to leave the group");
        }
        
        return "redirect:/groups";
    }

    @GetMapping("/{id}/invite")
    public String generateInvitation(
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
        
        // Only members can generate invitations
        if (!group.isMember(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be a member to invite others");
            return "redirect:/groups";
        }
        
        String invitationCode = studyGroupService.generateInvitationCode(group);
        String invitationLink = "/groups/join/" + invitationCode;
        
        model.addAttribute("group", group);
        model.addAttribute("invitationCode", invitationCode);
        model.addAttribute("invitationLink", invitationLink);
        
        return "groups/invite";
    }

    @GetMapping("/{id}/delete")
    public String deleteGroup(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        Optional<StudyGroup> groupOpt = studyGroupService.getGroupById(id);
        
        if (userOpt.isEmpty() || groupOpt.isEmpty()) {
            return "redirect:/groups";
        }
        
        User user = userOpt.get();
        StudyGroup group = groupOpt.get();
        
        // Only the creator can delete the group
        if (!group.getCreator().equals(user)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only the creator can delete the group");
            return "redirect:/groups";
        }
        
        boolean deleted = studyGroupService.deleteGroup(group);
        
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Group deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete the group");
        }
        
        return "redirect:/groups";
    }
}