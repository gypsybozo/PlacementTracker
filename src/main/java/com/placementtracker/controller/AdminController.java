package com.placementtracker.controller;

import com.placementtracker.model.Job;
import com.placementtracker.model.User;
import com.placementtracker.repository.JobRepository;
import com.placementtracker.service.JobService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final JobRepository jobRepository;

    @GetMapping("/jobs/manage")
    public String showJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "admin/jobs/manage";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("jobCount", jobService.findAllJobs().size());
        return "admin/dashboard";
    }
    
    // User Management
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }
    
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users?error=notfound";
        }
        
        model.addAttribute("user", userOpt.get());
        return "admin/users/view";
    }
    
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/users?success=statusupdated";
    }
    
    @PostMapping("/users/{id}/make-admin")
    public String makeUserAdmin(@PathVariable Long id) {
        userService.assignAdminRole(id);
        return "redirect:/admin/users?success=roleassigned";
    }
    
    @PostMapping("/users/{id}/remove-admin")
    public String removeAdminRole(@PathVariable Long id) {
        userService.removeAdminRole(id);
        return "redirect:/admin/users?success=roleremoved";
    }
    @GetMapping("/notifications/send")
    public String showSendNotificationForm(Model model) {
        model.addAttribute("notificationType", "all");

        // ✅ Add this to get all jobs (you can filter active ones too if needed)
        List<Job> jobs = jobRepository.findAll();
        model.addAttribute("jobs", jobs);

        return "admin/notifications/send";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users?success=deleted";
    }
    
    // Notification System
    @GetMapping("/notifications")
    public String notificationDashboard(Model model) {
        return "admin/notifications/dashboard";
    }
    
    
    @PostMapping("/notifications/send")
    public String sendNotification(@RequestParam String title,
                                @RequestParam String message,
                                @RequestParam String notificationType,
                                @RequestParam(required = false) Long jobId) {
        if ("GENERAL".equals(notificationType)) {
            userService.notifyAllUsers(title, message);
        } else if ("JOB".equals(notificationType) && jobId != null) {
            userService.notifyUsersAboutJob(jobId, title, message);
        } else {
            return "redirect:/admin/notifications?error=Invalid+notification+type";
        }

        return "redirect:/admin/notifications?success=true";
    }
    
    @PostMapping("/jobs/save")
    public String saveJob(@ModelAttribute Job job, RedirectAttributes redirectAttributes) {
        job.setPostedDate(LocalDateTime.now());
        jobRepository.save(job);

        redirectAttributes.addFlashAttribute("successMessage", "Job posted successfully!");
        return "redirect:/admin/jobs/manage";  // 👈 redirect back to the form
    }


}