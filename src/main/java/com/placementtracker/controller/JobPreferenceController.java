package com.placementtracker.controller;

import com.placementtracker.dto.JobPreferenceDto;
import com.placementtracker.model.JobPreference;
import com.placementtracker.model.User;
import com.placementtracker.service.JobPreferenceService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class JobPreferenceController {

    private final JobPreferenceService preferenceService;
    private final UserService userService;

    @GetMapping
    public String showPreferences(Model model, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        Optional<JobPreferenceDto> preferenceDtoOpt = preferenceService.findDtoByUser(user);
        
        JobPreferenceDto preferenceDto = preferenceDtoOpt.orElse(new JobPreferenceDto());
        model.addAttribute("preferenceDto", preferenceDto);
        
        return "preferences/edit";
    }
    
    @PostMapping("/update")
    public String updatePreferences(@Valid @ModelAttribute("preferenceDto") JobPreferenceDto preferenceDto,
                                  BindingResult result, Model model, 
                                  Authentication authentication) {
        if (result.hasErrors()) {
            return "preferences/edit";
        }

        System.out.println("Received update request");
        System.out.println("Companies: " + preferenceDto.getPreferredCompanies());
        System.out.println("Roles: " + preferenceDto.getPreferredRoles());
        System.out.println("Locations: " + preferenceDto.getPreferredLocations());
        System.out.println("Skills: " + preferenceDto.getPreferredSkills());
        System.out.println("Min Salary: " + preferenceDto.getMinSalary());
        System.out.println("Notify Enabled: " + preferenceDto.isNotifyEnabled());
    
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        preferenceService.createOrUpdatePreference(user, preferenceDto);
        
        return "redirect:/preferences?success=updated";
    }
    
    @PostMapping("/toggle-notifications")
    public String toggleNotifications(@RequestParam boolean enabled, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        preferenceService.toggleNotifications(user, enabled);
        
        return "redirect:/preferences?success=toggled";
    }
}