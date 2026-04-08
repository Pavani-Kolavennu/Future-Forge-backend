package com.futureforge.auth;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/auth", "/auth"})
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
	    return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/otp/send")
	public ResponseEntity<OtpSendResponseDto> sendOtp(@Valid @RequestBody OtpRequestDto request) {
		return ResponseEntity.ok(authService.sendOtp(request));
	}

	@PostMapping("/otp/verify")
	public ResponseEntity<OtpVerificationResponseDto> verifyOtp(@Valid @RequestBody OtpVerificationRequestDto request) {
		return ResponseEntity.ok(authService.verifyOtp(request));
	}
}
