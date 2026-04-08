package com.futureforge.user;

public record UpdateProfileDto(
		String fullName,
		String phone,
		String password
) {
}
