package com.futureforge.user;

public record UserProfileDto(
		Long id,
		String fullName,
		String email,
		Role role,
		boolean enabled
) {
}
