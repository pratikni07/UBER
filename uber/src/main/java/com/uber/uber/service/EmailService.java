package com.uber.uber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("pratik014pratik@gmail.com");

        try {
            javaMailSender.send(message);
            return true;
        } catch (MailException e) {
            return false;
        }
    }
}
