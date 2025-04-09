package com.placementtracker.service;

import com.placementtracker.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${app.verification-link}")
    private String verificationBaseUrl;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(User user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = "https://placementtracker-g0m1.onrender.com/register" + "/verify?token=" + token;
        
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Please confirm your account registration by clicking the link below:\n\n"
                + confirmationUrl + "\n\nThis link will expire in 24 hours.");
        
        mailSender.send(email);
    }
}