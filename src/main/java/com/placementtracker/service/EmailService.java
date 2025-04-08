package com.placementtracker.service;

import com.placementtracker.model.User;

public interface EmailService {
    void sendVerificationEmail(User user, String token);
}