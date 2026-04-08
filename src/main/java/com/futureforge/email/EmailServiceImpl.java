package com.futureforge.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendOtp(String to, String otp) {

        System.out.println("Sending email to: " + to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom("spoorthipaduchuri@gmail.com");
            message.setSubject("OTP Verification");
            message.setText("Your OTP is: " + otp);

            mailSender.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY ✅");

        } catch (Exception e) {
            System.out.println("EMAIL FAILED ❌");
            e.printStackTrace(); // 🔥 THIS IS KEY
            throw new RuntimeException("Email sending failed");
        }
    }
}