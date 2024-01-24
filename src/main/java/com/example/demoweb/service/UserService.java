package com.example.demoweb.service;

import com.example.demoweb.config.JwtTokenProvider;
import com.example.demoweb.dto.UserRequest;
import com.example.demoweb.dto.UserResponse;
import com.example.demoweb.entity.Role;
import com.example.demoweb.entity.User;
import com.example.demoweb.dto.UserDTO;
import com.example.demoweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    public UserService(PasswordEncoder passwordEncoder,UserRepository userRepository,JwtTokenProvider tokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user.getRole().toString())
        );
    }
    private List<GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
    public void register(UserDTO userDto) {
        User existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setEmail(userDto.getEmail());
        newUser.setRole(Role.USER);

        userRepository.save(newUser);
    }

    public String login(UserRequest userRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequest.getUsername(),
                            userRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                // Xác thực thành công, trả về JWT
                String jwt = tokenProvider.generateToken(userRequest.getUsername());
                return jwt;
            } else {
                // Xác thực không thành công
                throw new BadCredentialsException("Authentication failed");
            }
        } catch (AuthenticationException e) {
            // Xử lý trường hợp đăng nhập không thành công
            throw new RuntimeException("Login failed", e);
        }
    }

}
