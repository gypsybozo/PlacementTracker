package com.placementtracker.service;

import com.placementtracker.dto.UserRegistrationDto;
import com.placementtracker.model.User;

import java.util.Optional;

public interface UserService {
    User registerNewUser(UserRegistrationDto registrationDto);
    boolean verifyUser(String token);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    void saveLastLoginDate(User user);
}