package com.futureforge.auth;

import com.futureforge.user.Role;

public record AuthResponseDto(
		String token,
		Long userId,
		String fullName,
		String email,
		Role role
) {
}
