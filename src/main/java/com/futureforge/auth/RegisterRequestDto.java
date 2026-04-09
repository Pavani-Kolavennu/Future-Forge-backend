package com.futureforge.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.futureforge.user.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
		@JsonAlias({"name", "username"})
		@NotBlank(message = "Full name is required")
		String fullName,
		@JsonAlias({"userEmail"})
		@Email(message = "Email must be valid")
		@NotBlank(message = "Email is required")
		String email,
		@NotBlank(message = "Password is required")
		String password,
		@JsonProperty("role")
		Role role
) {
}
