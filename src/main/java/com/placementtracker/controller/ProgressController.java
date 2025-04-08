package com.placementtracker.controller;

import com.placementtracker.dto.UserProgressDto;
import com.placementtracker.model.User;
import com.placementtracker.model.UserProgress;
import com.placementtracker.service.ProgressService;
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
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final UserService userService;

    @GetMapping
    public String viewProgressList(Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        List<UserProgress> progressList = progressService.getUserProgress(user);
        
        model.addAttribute("progressList", progressList);
        model.addAttribute("totalProblems", progressService.getTotalProblemsSolved(user));
        model.addAttribute("easyProblems", progressService.getProblemsSolvedByDifficulty(user, "EASY"));
        model.addAttribute("mediumProblems", progressService.getProblemsSolvedByDifficulty(user, "MEDIUM"));
        model.addAttribute("hardProblems", progressService.getProblemsSolvedByDifficulty(user, "HARD"));
        model.addAttribute("currentStreak", progressService.getCurrentStreak(user));
        
        return "progress/list";
    }

    @GetMapping("/add")
    public String showAddProgressForm(Model model) {
        model.addAttribute("progressDto", new UserProgressDto());
        return "progress/add";
    }

    @PostMapping("/add")
    public String addProgress(
            @Valid @ModelAttribute("progressDto") UserProgressDto progressDto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "progress/add";
        }
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        try {
            User user = userOpt.get();
            progressService.recordProgress(user, progressDto);
            redirectAttributes.addFlashAttribute("successMessage", "Progress recorded successfully!");
            return "redirect:/progress";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording progress: " + e.getMessage());
            return "redirect:/progress/add";
        }
    }

    @GetMapping("/analytics")
    public String viewAnalytics(Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        
        model.addAttribute("totalProblems", progressService.getTotalProblemsSolved(user));
        model.addAttribute("platformStats", progressService.getProblemsByPlatform(user));
        model.addAttribute("difficultyStats", progressService.getProblemsByDifficulty(user));
        model.addAttribute("currentStreak", progressService.getCurrentStreak(user));
        
        return "progress/analytics";
    }
}