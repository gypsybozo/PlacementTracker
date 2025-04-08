package com.placementtracker.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String showDashboard(Authentication authentication, Model model) {
        // Here you would typically load user-specific data for the dashboard
        // For now, we'll just return the view
        return "dashboard";
    }
}