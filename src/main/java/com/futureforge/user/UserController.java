package com.futureforge.user;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public List<UserProfileDto> getAllUsers() {
		return userService.findAll().stream().map(userService::toProfileDto).toList();
	}

	@GetMapping("/{userId}")
	public UserProfileDto getUser(@PathVariable Long userId) {
		return userService.getProfile(userId);
	}

	@PutMapping("/{userId}")
	public UserProfileDto updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateProfileDto dto) {
		return userService.updateProfile(userId, dto);
	}
}
