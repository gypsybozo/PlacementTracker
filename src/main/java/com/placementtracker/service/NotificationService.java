package com.placementtracker.service;

import com.placementtracker.model.Job;
import com.placementtracker.model.Notification;
import com.placementtracker.model.User;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    void createJobNotification(User user, Job job);
    List<Notification> getUserNotifications(User user);
    List<Notification> getUserUnreadNotifications(User user);
    void markNotificationAsRead(Long notificationId);
    void markAllNotificationsAsRead(User user);
    long getUnreadNotificationCount(User user);
    void checkAndSendNotifications();
    Optional<Notification> findById(Long id);
}