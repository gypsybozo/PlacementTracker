package com.placementtracker.controller;

import com.placementtracker.model.Notification;
import com.placementtracker.model.User;
import com.placementtracker.service.NotificationService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public String showNotifications(Model model, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        List<Notification> notifications = notificationService.getUserNotifications(user);
        model.addAttribute("notifications", notifications);
        
        return "notifications/list";
    }
    
    @GetMapping("/unread")
    public String showUnreadNotifications(Model model, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        List<Notification> unreadNotifications = notificationService.getUserUnreadNotifications(user);
        model.addAttribute("notifications", unreadNotifications);
        model.addAttribute("unreadOnly", true);
        
        return "notifications/list";
    }
    
    @PostMapping("/mark-read/{id}")
    public String markNotificationAsRead(@PathVariable Long id, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        notificationService.markNotificationAsRead(id);
        
        return "redirect:/notifications";
    }
    
    @PostMapping("/mark-all-read")
    public String markAllNotificationsAsRead(Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        notificationService.markAllNotificationsAsRead(user);
        
        return "redirect:/notifications?success=allread";
    }
    
    @GetMapping("/view/{id}")
    public String viewNotification(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Optional<Notification> notificationOpt = notificationService.findById(id);
        if (notificationOpt.isEmpty()) {
            return "redirect:/notifications?error=notfound";
        }
        
        Notification notification = notificationOpt.get();
        
        // Make sure this notification belongs to the current user
        if (!notification.getUser().getId().equals(userOpt.get().getId())) {
            return "redirect:/notifications?error=unauthorized";
        }
        
        // Mark as read
        notificationService.markNotificationAsRead(id);
        
        model.addAttribute("notification", notification);
        return "notifications/view";
    }
}