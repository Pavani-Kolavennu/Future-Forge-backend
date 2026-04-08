package com.futureforge.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpVerificationRequestDto(
		@Email(message = "Email must be valid")
		@NotBlank(message = "Email is required")
		String email,
		@NotBlank(message = "OTP is required")
		String otp
) {
}