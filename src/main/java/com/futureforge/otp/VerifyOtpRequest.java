package com.futureforge.otp;

public record VerifyOtpRequest(
    String email,
    String otp
) {}