package com.futureforge.otp;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futureforge.email.EmailService;

import jakarta.transaction.Transactional;
@Transactional
@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public void sendOtp(String email) {

        otpRepository.deleteByEmail(email);

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        OtpEntity entity = new OtpEntity();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        entity.setVerified(false);

        otpRepository.save(entity);

        System.out.println("OTP GENERATED: " + otp);

        try {
            emailService.sendOtp(email, otp);
            System.out.println("EMAIL SENT SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 THIS WILL SHOW REAL ERROR
            throw new RuntimeException("Email sending failed");
        }
    }

    @Override
    public boolean verifyOtp(String email, String otp) {

        Optional<OtpEntity> optional =
            otpRepository.findTopByEmailOrderByExpiryTimeDesc(email);

        if (optional.isEmpty()) {
            System.out.println("No OTP found for email");
            return false;
        }

        OtpEntity entity = optional.get();

        System.out.println("ENTERED OTP: " + otp);
        System.out.println("DB OTP: " + entity.getOtp());

        if (entity.getOtp().equals(otp) &&
            entity.getExpiryTime().isAfter(LocalDateTime.now())) {

            entity.setVerified(true);
            otpRepository.save(entity);
            return true;
        }

        return false;
    }
}