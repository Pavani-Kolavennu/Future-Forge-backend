package com.futureforge.auth;

public interface AuthService {

	AuthResponseDto register(RegisterRequestDto request);

	AuthResponseDto login(AuthRequestDto request);
}
