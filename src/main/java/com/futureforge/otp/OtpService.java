package com.futureforge.otp;

public interface OtpService {
    void sendOtp(String email);
    boolean verifyOtp(String email, String otp);
}