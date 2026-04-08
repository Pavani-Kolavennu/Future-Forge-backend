package com.futureforge.auth;

public record OtpSendResponseDto(
		boolean success,
		String message
) {
}