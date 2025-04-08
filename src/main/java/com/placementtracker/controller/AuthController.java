package com.placementtracker.controller;

import com.placementtracker.dto.LoginDto;
import com.placementtracker.model.User;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        try {
            // First check if user exists and is enabled
            Optional<User> userOpt = userService.findByUsername(loginDto.getUsername());
            if (userOpt.isEmpty() || !userOpt.get().isEnabled()) {
                model.addAttribute("error", "Invalid username or account not verified");
                return "login";
            }
            
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Update last login time
            userService.saveLastLoginDate(userOpt.get());
            
            return "redirect:/dashboard";
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }
}