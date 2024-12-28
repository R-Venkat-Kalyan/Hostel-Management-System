//package com.example.hms_fe1.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Component;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//@Component
//public class MailService {
//
//	@Value("${spring.mail.username}")
//    private String from;
//
//    @Autowired
//    private JavaMailSender javaMailSender;
//
//    public void sendMail(String to, String subject, String body) throws MessagingException {
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper;
//        helper = new MimeMessageHelper(message, true);//true indicates multipart message
//
//        helper.setFrom(from); // <--- THIS IS IMPORTANT
//
//        helper.setSubject(subject);
//        helper.setTo(to);
//        helper.setText(body, true);//true indicates body is html
//        javaMailSender.send(message);
//    }
//}
