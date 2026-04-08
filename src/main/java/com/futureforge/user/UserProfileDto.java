package com.futureforge.user;

public record UserProfileDto(
		Long id,
		String fullName,
		String email,
		String phone,
		Role role,
		boolean enabled
) {
}
