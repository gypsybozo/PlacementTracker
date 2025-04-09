package com.placementtracker.service;

import com.placementtracker.model.Job;
import com.placementtracker.model.JobPreference;
import com.placementtracker.model.Notification;
import com.placementtracker.model.User;
import com.placementtracker.repository.JobPreferenceRepository;
import com.placementtracker.repository.JobRepository;
import com.placementtracker.repository.NotificationRepository;
import com.placementtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobPreferenceRepository preferenceRepository;
    private final EmailService emailService;
    private final JobService jobService;

    @Override
    @Transactional
    public void createJobNotification(User user, Job job) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setJob(job);
        notification.setTitle("New Job Opportunity: " + job.getTitle() + " at " + job.getCompany());
        notification.setMessage("A new job matching your preferences has been posted: " + job.getTitle() + " at " + job.getCompany() + " in " + job.getLocation());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        
        notificationRepository.save(notification);
        
        // Handle email sending if needed
        sendJobNotificationEmail(notification);
    }

    @Override
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Notification> getUserUnreadNotifications(User user) {
        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
        LocalDateTime now = LocalDateTime.now();
        
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(now);
        });
        
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Override
    @Scheduled(cron = "0 0 */6 * * *") // Run every 6 hours
    @Transactional
    public void checkAndSendNotifications() {
        // Get recent jobs (posted in the last day)
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<Job> recentJobs = jobRepository.findRecentJobs(oneDayAgo);
        
        if (recentJobs.isEmpty()) {
            return;
        }
        
        // Get all users with preferences
        List<JobPreference> allPreferences = preferenceRepository.findAll();
        
        for (JobPreference preference : allPreferences) {
            if (!preference.isNotifyEnabled()) {
                continue;
            }
            
            User user = preference.getUser();
            List<Job> matchingJobs = findMatchingJobs(preference, recentJobs);
            
            for (Job job : matchingJobs) {
                createJobNotification(user, job);
            }
        }
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }
    
    private List<Job> findMatchingJobs(JobPreference preference, List<Job> jobs) {
        return jobs.stream()
                .filter(job -> isJobMatching(preference, job))
                .collect(Collectors.toList());
    }
    
    private boolean isJobMatching(JobPreference preference, Job job) {
        // Check company match
        boolean companyMatch = preference.getPreferredCompanies().isEmpty() || 
                               preference.getPreferredCompanies().contains(job.getCompany());
                               
        // Check location match
        boolean locationMatch = preference.getPreferredLocations().isEmpty() || 
                                preference.getPreferredLocations().contains(job.getLocation());
                                
        // Check role/title match
        boolean roleMatch = preference.getPreferredRoles().isEmpty() || 
                           preference.getPreferredRoles().stream()
                               .anyMatch(role -> job.getTitle().toLowerCase().contains(role.toLowerCase()));
                               
        // Check skills match
        boolean skillsMatch = preference.getPreferredSkills().isEmpty() || 
                             (job.getSkillsRequired() != null && 
                             preference.getPreferredSkills().stream()
                                 .anyMatch(skill -> job.getSkillsRequired().toLowerCase().contains(skill.toLowerCase())));
        
        // Match if any criteria matches
        return companyMatch || locationMatch || roleMatch || skillsMatch;
    }
    
    private void sendJobNotificationEmail(Notification notification) {
        // Only send email if not already sent
        if (!notification.isEmailSent()) {
            User user = notification.getUser();
            Job job = notification.getJob();
            
            // Prepare email content
            String subject = "New Job Opportunity: " + job.getTitle() + " at " + job.getCompany();
            
            StringBuilder content = new StringBuilder();
            content.append("Hello ").append(user.getUsername()).append(",\n\n");
            content.append("We found a job matching your preferences:\n\n");
            content.append("Title: ").append(job.getTitle()).append("\n");
            content.append("Company: ").append(job.getCompany()).append("\n");
            content.append("Location: ").append(job.getLocation()).append("\n\n");
            
            if (job.getDescription() != null && !job.getDescription().isEmpty()) {
                content.append("Description: ").append(job.getDescription()).append("\n\n");
            }
            
            if (job.getSkillsRequired() != null && !job.getSkillsRequired().isEmpty()) {
                content.append("Required Skills: ").append(job.getSkillsRequired()).append("\n\n");
            }
            
            if (job.getSalaryRange() != null && !job.getSalaryRange().isEmpty()) {
                content.append("Salary Range: ").append(job.getSalaryRange()).append("\n\n");
            }
            
            // Add application link
            content.append("Log in to your account to see more details and apply.\n\n");
            content.append("Good luck with your job search!\n");
            content.append("Placement Tracker Team");
            
            // Send email
            sendEmailToUser(user, subject, content.toString());
            
            // Update notification as email sent
            notification.setEmailSent(true);
            notification.setEmailSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }
    
    private void sendEmailToUser(User user, String subject, String content) {
        // Call external email service
        // This is a placeholder. We should use the EmailService implementation
        // For now, we'll just log that we would send an email
        System.out.println("Sending email to: " + user.getEmail());
        System.out.println("Subject: " + subject);
        System.out.println("Content: " + content);
    }
}