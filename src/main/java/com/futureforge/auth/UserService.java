package com.futureforge.auth;

import java.util.List;
import java.util.Optional;

import com.futureforge.user.User;

public interface UserService {

	Optional<User> findById(Long id);

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	List<User> findAll();

	User save(User user);
}

