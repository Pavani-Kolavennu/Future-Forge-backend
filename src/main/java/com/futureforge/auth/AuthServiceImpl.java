package com.futureforge.auth;

import com.futureforge.common.UnauthorizedException;
import com.futureforge.common.ValidationException;
import com.futureforge.user.Role;
import com.futureforge.user.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserService userService,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {

        if (userService.existsByEmail(request.email())) {
            throw new ValidationException("Email already exists");
        }

        User user = new User();

        user.setFullName(request.fullName());
        user.setPassword(passwordEncoder.encode(request.password()));

        // email setter not present → assign directly using constructor alternative
        user = new User(
                request.fullName(),
                request.email().toLowerCase(),
                passwordEncoder.encode(request.password()),
                request.role() == null ? Role.CANDIDATE : request.role()
        );

        return toResponse(userService.save(user));
    }

    @Override
    public AuthResponseDto login(AuthRequestDto request) {

        User user = userService.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User account is disabled");
        }

        return toResponse(user);
    }

    private AuthResponseDto toResponse(User user) {
        return new AuthResponseDto(
                jwtService.generateToken(user),
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}