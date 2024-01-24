package com.example.demoweb.controller;

import com.example.demoweb.dto.UserDTO;
import com.example.demoweb.dto.UserRequest;
import com.example.demoweb.dto.UserResponse;
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
}
