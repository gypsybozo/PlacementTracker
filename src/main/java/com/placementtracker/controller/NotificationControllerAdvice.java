package com.placementtracker.controller;

import com.placementtracker.model.User;
import com.placementtracker.service.NotificationService;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
public class NotificationControllerAdvice {

    private final NotificationService notificationService;
    private final UserService userService;

    @ModelAttribute("unreadNotificationCount")
    public long getUnreadNotificationCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() 
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return 0;
        }
        
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return 0;
        }
        
        return notificationService.getUnreadNotificationCount(userOpt.get());
    }
}