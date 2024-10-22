package com.uber.uber.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpService {
    private static final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    public String generateOtp() {
        // Generate a random number between 0 and 999999 (6 digits)
        int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("%06d", otp);
    }
}
