package com.placementtracker.controller;

import com.placementtracker.model.User;
import com.placementtracker.service.ProgressService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final ProgressService progressService;

    @GetMapping
    public String showDashboard(Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        
        // Add progress statistics to dashboard
        model.addAttribute("totalProblems", progressService.getTotalProblemsSolved(user));
        model.addAttribute("currentStreak", progressService.getCurrentStreak(user));
        model.addAttribute("easyProblems", progressService.getProblemsSolvedByDifficulty(user, "EASY"));
        model.addAttribute("mediumProblems", progressService.getProblemsSolvedByDifficulty(user, "MEDIUM"));
        model.addAttribute("hardProblems", progressService.getProblemsSolvedByDifficulty(user, "HARD"));
        
        return "dashboard";
    }
}