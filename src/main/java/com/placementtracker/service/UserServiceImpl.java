package com.placementtracker.service;

import com.placementtracker.dto.UserRegistrationDto;
import com.placementtracker.model.User;
import com.placementtracker.model.VerificationToken;
import com.placementtracker.repository.UserRepository;
import com.placementtracker.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
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
        roles.add("ROLE_USER");
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
}