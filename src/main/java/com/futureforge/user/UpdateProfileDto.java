package com.futureforge.user;

public record UpdateProfileDto(
		String fullName,
		String password
) {
}
