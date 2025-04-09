package com.placementtracker.service;

import com.placementtracker.dto.UserRegistrationDto;
import com.placementtracker.model.Job;
import com.placementtracker.model.Notification;
import com.placementtracker.model.User;
import com.placementtracker.model.VerificationToken;
import com.placementtracker.repository.JobRepository;
import com.placementtracker.repository.NotificationRepository;
import com.placementtracker.repository.UserRepository;
import com.placementtracker.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final NotificationRepository notificationRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public User registerNewUser(UserRegistrationDto registrationDto) {

        System.out.println("Registering: " + registrationDto.getUsername());

        // Validate passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords don't match");
        }
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }


        // Create new user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        // Assign default role
        Set<String> roles = new HashSet<>();
        if (registrationDto.getUsername().equals("admin")) {
            roles.add("ROLE_ADMIN");
        }
        else {
            roles.add("ROLE_USER");
        }
        user.setRoles(roles);


        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Create verification token
        VerificationToken verificationToken = new VerificationToken(savedUser);
        tokenRepository.save(verificationToken);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser, verificationToken.getToken());
        
        return savedUser;
    }

    @Override
    @Transactional
    public boolean verifyUser(String token) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);
        
        if (verificationToken.isPresent() && !verificationToken.get().isExpired()) {
            User user = verificationToken.get().getUser();
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
        
        return false;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void saveLastLoginDate(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public boolean toggleUserStatus(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean assignAdminRole(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Set<String> roles = user.getRoles();
            roles.add("ROLE_ADMIN");
            user.setRoles(roles);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean removeAdminRole(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Set<String> roles = user.getRoles();
            roles.remove("ROLE_ADMIN");
            user.setRoles(roles);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void notifyAllUsers(String title, String message) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);
            notificationRepository.save(notification);
            
            // Optionally send email
            // emailService.sendNotificationEmail(user.getEmail(), title, message);
        }
    }

    @Transactional
    @Override
    public void notifyUsersAboutJob(Long jobId, String title, String message) {
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            return;
        }
        
        Job job = jobOpt.get();
        List<User> interestedUsers = userRepository.findUsersInterestedInJobCriteria(
            job.getCompany(), 
            job.getLocation(), 
            new ArrayList<>(job.getTags()) // if tags is a Set
        );


        
        for (User user : interestedUsers) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setJob(job);
            notification.setTitle(title != null ? title : "New Job Matching Your Preferences");
            notification.setMessage(message != null ? message : 
                "A new job at " + job.getCompany() + " matches your preferences: " + job.getTitle());
            notification.setRelatedJobId(jobId);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);
            notificationRepository.save(notification);
            
            // // Optionally send email
            // emailService.sendJobNotificationEmail(user.getEmail(), notification.getTitle(), 
            //     notification.getMessage(), job);
        }
    }

}