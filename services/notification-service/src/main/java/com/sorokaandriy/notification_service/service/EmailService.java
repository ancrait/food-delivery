package com.sorokaandriy.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.verification-url}")
    private String verificationUrl;

    public boolean sendVerificationEmail(String to, String firstName, String verificationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Verify your email");

            String link = verificationUrl + verificationToken;

            String body = String.format("""
                    <h2>Hello, %s!</h2>
                    <p>Thank you for registering. Please verify your email by clicking the link below:</p>
                    <p><a href="%s">Verify Email</a></p>
                    <p>This link expires in 24 hours.</p>
                    """, firstName, link);

            helper.setText(body, true);

            mailSender.send(message);
            log.info("Verification email sent to {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
