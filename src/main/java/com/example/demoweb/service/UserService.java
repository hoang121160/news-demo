package com.example.demoweb.service;

import com.example.demoweb.config.JwtTokenProvider;
import com.example.demoweb.dto.request.UserRequest;
import com.example.demoweb.enums.Role;
import com.example.demoweb.entity.User;
import com.example.demoweb.dto.UserDTO;
import com.example.demoweb.exception.UserNotFoundException;
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

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    public UserService(PasswordEncoder passwordEncoder,UserRepository userRepository,JwtTokenProvider tokenProvider,
                       EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
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
    public void requestPasswordReset(String email)throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        //Tạo và lưu token đặt lại mật khẩu
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiration(calculateTokenExpirationDate());
        userRepository.save(user);
        emailService.sendResetPasswordEmail(email, resetToken);
    }
    public void confirmPasswordReset(String email, String token, String newPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found with email: \" + email)"));
        if (!user.getResetPasswordToken().equals(token) || isTokenExpired(user.getResetPasswordTokenExpiration())){
            throw new IllegalArgumentException("Invalid or expired reset password token.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    private Date calculateTokenExpirationDate() {
        // Logic để tính thời gian hết hạn của token
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        return calendar.getTime();
    }
    private boolean isTokenExpired(Date expirationDate) {
        // Kiểm tra xem token có hết hạn hay không
        return expirationDate != null && expirationDate.before(new Date());
    }

}
