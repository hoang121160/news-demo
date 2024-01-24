package com.example.demoweb.controller;

import com.example.demoweb.dto.UserDTO;
import com.example.demoweb.dto.UserRequest;
import com.example.demoweb.exception.UserNotFoundException;
import com.example.demoweb.service.EmailService;
import com.example.demoweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO){
        try {
            userService.register(userDTO);
            return ResponseEntity.ok("User registered successfully !");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserRequest userRequest){
        try {
            String jwt = userService.login(userRequest);
            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }
    @PostMapping("/request-reset-password")
    public ResponseEntity<String> requestResetPassword(@RequestParam String email){
        try {
            userService.requestPasswordReset(email);
            return ResponseEntity.ok("Password reset request sent successfully. Please check your email.");
        }catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/confirm-reset-password")
    public ResponseEntity<String> confirmPasswordReset(@RequestParam String email,
                                                       @RequestParam String token,
                                                       @RequestParam String newPassword) {
        try {
            userService.confirmPasswordReset(email,token, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        }catch (IllegalArgumentException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
