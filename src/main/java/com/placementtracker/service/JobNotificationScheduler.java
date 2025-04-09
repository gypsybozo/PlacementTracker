package com.placementtracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class JobNotificationScheduler {

    private final NotificationService notificationService;

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void processJobNotifications() {
        notificationService.checkAndSendNotifications();
    }
}