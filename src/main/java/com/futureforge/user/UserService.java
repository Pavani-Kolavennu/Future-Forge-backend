package com.futureforge.user;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.futureforge.common.ResourceNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements com.futureforge.auth.UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(Objects.requireNonNull(id, "id is required"));
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User save(User user) {
		return userRepository.save(Objects.requireNonNull(user, "user is required"));
	}

	public UserProfileDto toProfileDto(User user) {
		return new UserProfileDto(user.id, user.fullName, user.email, user.role, user.enabled);
	}

	public UserProfileDto getProfile(Long userId) {
		return userRepository.findById(Objects.requireNonNull(userId, "userId is required"))
				.map(this::toProfileDto)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	public UserProfileDto updateProfile(Long userId, UpdateProfileDto dto) {
		User user = userRepository.findById(Objects.requireNonNull(userId, "userId is required"))
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (dto.fullName() != null && !dto.fullName().isBlank()) {
			user.fullName = dto.fullName();
		}
		
		if (dto.password() != null && !dto.password().isBlank()) {
			user.password = passwordEncoder.encode(dto.password());
		}

		return toProfileDto(userRepository.save(Objects.requireNonNull(user, "user is required")));
	}
}
