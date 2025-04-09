package com.placementtracker.config;

import com.placementtracker.model.User;
import com.placementtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

@Configuration
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@example.com");
            user.setPassword(passwordEncoder.encode("adminpassword"));

            Set<String> roles = new HashSet<>();
            roles.add("ROLE_ADMIN");
            user.setRoles(roles);

            System.out.println("✅ Admin user created: admin@example.com / admin123");
        }
    }
}
