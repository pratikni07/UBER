package com.uber.uber.service;

import com.uber.uber.model.UserSchema;
import com.uber.uber.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    public UserSchema registerUser(UserSchema user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalStateException("User with email " + user.getEmail() + " already exists");
        }
        if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Name, email, and password are required");
        }
        System.out.println(user.getEmail());
        UserSchema savedUser = userRepository.save(user);
        sendWelcomeEmail(savedUser);
        return savedUser;
    }

    private void sendWelcomeEmail(UserSchema user) {
        String subject = "Welcome to Uber";
        String text = "Hello " + user.getName() + ",\n\n" +
                "Welcome to Uber! We are excited to have you on board. " +
                "We are here to make your life easier. " +
                "Please let us know if you have any questions or concerns. " +
                "We are here to help you. " +
                "Thank you for choosing Uber. " +
                "Have a great day!";

        boolean mailSent = emailService.sendMail(user.getEmail(), subject, text);
        if (mailSent) {
            System.out.println("Welcome email sent successfully to " + user.getEmail());
        } else {
            System.out.println("Failed to send welcome email to " + user.getEmail());
        }
    }

    public UserSchema loginUser(String email, String pass) {
        System.out.println(email+" "+pass);
        UserSchema existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new IllegalArgumentException("User with email " + email + " does not exist");
        }
        if (!existingUser.getPassword().equals(pass)) {
            throw new IllegalArgumentException("Incorrect password");
        }
        // create token

        return existingUser;
    }
}