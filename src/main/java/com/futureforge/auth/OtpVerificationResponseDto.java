package com.futureforge.auth;

public record OtpVerificationResponseDto(
		boolean verified,
		String message
) {
}