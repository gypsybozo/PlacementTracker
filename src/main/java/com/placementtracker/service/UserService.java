package com.placementtracker.service;

import com.placementtracker.dto.UserRegistrationDto;
import com.placementtracker.model.User;

import java.util.Optional;
import java.util.List;

public interface UserService {
    User registerNewUser(UserRegistrationDto registrationDto);
    boolean verifyUser(String token);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    void saveLastLoginDate(User user);

    // Admin methods
    List<User> findAllUsers();
    Optional<User> findById(Long id);
    long countUsers();
    boolean toggleUserStatus(Long id);
    boolean assignAdminRole(Long id);
    boolean removeAdminRole(Long id);
    boolean deleteUser(Long id);
    
    // Notification methods
    void notifyAllUsers(String title, String message);
    void notifyUsersAboutJob(Long jobId, String title, String message);
}