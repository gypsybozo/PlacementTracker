package com.placementtracker.controller;

import com.placementtracker.dto.JobDto;
import com.placementtracker.model.Job;
import com.placementtracker.model.User;
import com.placementtracker.service.JobService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final UserService userService;

    @GetMapping
    public String listJobs(Model model, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        List<Job> activeJobs = jobService.findActiveJobs();
        model.addAttribute("jobs", activeJobs);
        
        return "jobs/list";
    }
    
    @GetMapping("/view/{id}")
    public String viewJob(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Optional<Job> jobOpt = jobService.findById(id);
        if (jobOpt.isEmpty()) {
            return "redirect:/jobs?error=notfound";
        }
        
        model.addAttribute("job", jobOpt.get());
        
        return "jobs/view";
    }
    
    @GetMapping("/recommended")
    public String recommendedJobs(Model model, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        List<Job> recommendedJobs = jobService.findJobsMatchingUserPreferences(userOpt.get().getId());
        model.addAttribute("jobs", recommendedJobs);
        model.addAttribute("isRecommended", true);
        
        return "jobs/list";
    }
    
    @GetMapping("/search")
    public String searchJobs(@RequestParam(required = false) String keyword, 
                           @RequestParam(required = false) String location,
                           Model model, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        // For simple implementation, we'll just show all jobs
        List<Job> jobs = jobService.findActiveJobs();
        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        
        return "jobs/list";
    }
    
    // Admin Job Management - these would be protected by ADMIN role in SecurityConfig
    
    @GetMapping("/admin/create")
    public String showCreateJobForm(Model model) {
        model.addAttribute("jobDto", new JobDto());
        return "jobs/admin/create";
    }
    
    @PostMapping("/admin/create")
    public String createJob(@Valid @ModelAttribute("jobDto") JobDto jobDto,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "jobs/admin/create";
        }
        
        jobService.createJob(jobDto);
        return "redirect:/jobs/admin/manage?success=created";
    }
    
    @GetMapping("/admin/edit/{id}")
    public String showEditJobForm(@PathVariable Long id, Model model) {
        Optional<Job> jobOpt = jobService.findById(id);
        if (jobOpt.isEmpty()) {
            return "redirect:/jobs/admin/manage?error=notfound";
        }
        
        Job job = jobOpt.get();
        JobDto jobDto = convertToDto(job);
        model.addAttribute("jobDto", jobDto);
        model.addAttribute("jobId", id);
        
        return "jobs/admin/edit";
    }
    
    @PostMapping("/admin/edit/{id}")
    public String updateJob(@PathVariable Long id,
                          @Valid @ModelAttribute("jobDto") JobDto jobDto,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("jobId", id);
            return "jobs/admin/edit";
        }
        
        jobService.updateJob(id, jobDto);
        return "redirect:/jobs/admin/manage?success=updated";
    }
    
    @GetMapping("/admin/manage")
    public String manageJobs(Model model) {
        List<Job> allJobs = jobService.findAllJobs();
        model.addAttribute("jobs", allJobs);
        return "jobs/admin/manage";
    }
    
    @PostMapping("/admin/delete/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return "redirect:/jobs/admin/manage?success=deleted";
    }
    
    private JobDto convertToDto(Job job) {
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setLocation(job.getLocation());
        dto.setDescription(job.getDescription());
        dto.setSkillsRequired(job.getSkillsRequired());
        dto.setExperienceYears(job.getExperienceYears());
        dto.setPostedDate(job.getPostedDate());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        dto.setSalaryRange(job.getSalaryRange());
        dto.setTags(job.getTags());
        dto.setActive(job.isActive());
        return dto;
    }
}