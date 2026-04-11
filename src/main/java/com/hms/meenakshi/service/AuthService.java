package com.hms.meenakshi.service;

import com.hms.meenakshi.entity.VacatedResident;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public String generateOTP() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public void sendOtpMail(String toEmail, String otp, String name) throws MessagingException {
        try {
            // 1. Read the Template from the ClassPath (templates/Mail.html)
            Resource resource = new ClassPathResource("templates/otp-mail.html");
            byte[] encoded = Files.readAllBytes(resource.getFile().toPath());
            String content = new String(encoded, StandardCharsets.UTF_8);

            // 2. Replace the placeholders
            content = content.replace("[[otp]]", otp);
            content = content.replace("[[name]]", name);

            // 3. Create the MimeMessage for HTML support
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Security Verification Code: " + otp);
            helper.setText(content, true); // The 'true' flag renders it as HTML

            mailSender.send(message);
        } catch (IOException e) {
            throw new MessagingException("Failed to read email template", e);
        }
    }

    @Async
    public void sendVacateNoticeAsync(VacatedResident data, String residentEmail) {
        try {
            // 1. Load the High-End Template
            Resource resource = new ClassPathResource("templates/manager-pages/vacate-notice.html");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);

            // 2. Map Placeholders
            content = content.replace("[[name]]", data.getName())
                    .replace("[[uniId]]", data.getUniversityId())
                    .replace("[[room]]", data.getRoomNumber())
                    .replace("[[joinDate]]", data.getJoinedDate().toString())
                    .replace("[[vacateDate]]", data.getVacatedDate().toString())
                    .replace("[[totalPaid]]", String.format("%.2f", data.getTotalPaidDuringStay()))
                    .replace("[[reason]]", data.getReason().replace("_", " "));

            // 3. Prepare Recipients
            String[] recipients = {"owner@meenakshi.com", "manager@meenakshi.com", residentEmail};

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipients);
            helper.setSubject("Official Clearance: " + data.getName() + " [" + data.getReason() + "]");
            helper.setText(content, true);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send high-end vacate notice: " + e.getMessage());
        }
    }
}
