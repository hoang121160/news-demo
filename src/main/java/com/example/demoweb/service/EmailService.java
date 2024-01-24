package com.example.demoweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    public void sendResetPasswordEmail(String recipientEmail, String resetToken){
        String emailContent = "Click the link below to reset your password:\n\n" + resetToken;

        // Gửi email
        sendEmail(recipientEmail, "Reset Password", emailContent);
    }
    private void sendEmail(String recipientEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom(senderEmail);
        javaMailSender.send(mailMessage);
    }
}
