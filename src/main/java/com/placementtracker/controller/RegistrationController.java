package com.placementtracker.controller;

import com.placementtracker.dto.UserRegistrationDto;
import com.placementtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        System.out.println("Rendering registration form");
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                               BindingResult result, Model model) {

        System.out.println("POST /register hit");
        System.out.println(userDto);

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerNewUser(userDto);
            model.addAttribute("message", "Registration successful! Please check your email to verify your account.");
            return "register-success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token, Model model) {
        boolean verified = userService.verifyUser(token);
        
        if (verified) {
            model.addAttribute("message", "Your account has been successfully verified. You can now log in.");
            return "verification-success";
        } else {
            model.addAttribute("error", "The verification link is invalid or has expired.");
            return "verification-failure";
        }
    }
}