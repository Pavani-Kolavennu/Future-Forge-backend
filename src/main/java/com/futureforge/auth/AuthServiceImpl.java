package com.futureforge.auth;

import com.futureforge.common.UnauthorizedException;
import com.futureforge.common.ValidationException;
import com.futureforge.user.Role;
import com.futureforge.user.User;

import okhttp3.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
	private static final Duration OTP_TTL = Duration.ofMinutes(10);
	private static final SecureRandom RANDOM = new SecureRandom();

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

	public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
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
		user.fullName = request.fullName();
		user.email = request.email().toLowerCase();
		user.password = passwordEncoder.encode(request.password());	
		user.role = request.role() == null ? Role.CANDIDATE : request.role();
		user.enabled = true;

		return toResponse(userService.save(user));
	}

	@Override
	public AuthResponseDto login(AuthRequestDto request) {
		User user = userService.findByEmail(request.email().trim().toLowerCase())
		        .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

		if (!passwordEncoder.matches(request.password(), user.password)) {
		    throw new UnauthorizedException("Invalid email or password");
		}
		if (!user.enabled) {
			throw new UnauthorizedException("User account is disabled");
		}

		return toResponse(user);
	}

	@Override
	public OtpSendResponseDto sendOtp(OtpRequestDto request) {
		String email = normalizeEmail(request.email());
		String otp = generateOtp();
		otpStore.put(email, new OtpEntry(otp, Instant.now().plus(OTP_TTL)));

		boolean delivered = trySendOtpEmail(email, otp);
		String message = delivered
				? "OTP sent to email"
				: "OTP generated. Email delivery is not available, but verification can continue in this environment.";
		return new OtpSendResponseDto(true, message);
	}

	@Override
	public OtpVerificationResponseDto verifyOtp(OtpVerificationRequestDto request) {
		String email = normalizeEmail(request.email());
		String submittedOtp = request.otp().trim();
		OtpEntry storedOtp = otpStore.get(email);

		if (storedOtp == null || storedOtp.isExpired()) {
			otpStore.remove(email);
			return new OtpVerificationResponseDto(false, "OTP expired or not found");
		}

		boolean matches = storedOtp.code().equals(submittedOtp);
		if (matches) {
			otpStore.remove(email);
			return new OtpVerificationResponseDto(true, "OTP verified successfully");
		}

		return new OtpVerificationResponseDto(false, "Invalid OTP");
	}

	private AuthResponseDto toResponse(User user) {
		return new AuthResponseDto(jwtService.generateToken(user), user.id, user.fullName, user.email, user.role);
	}

	private String normalizeEmail(String email) {
		if (email == null || email.isBlank()) {
			throw new ValidationException("Email is required");
		}
		return email.trim().toLowerCase();
	}

	private String generateOtp() {
		int code = RANDOM.nextInt(900000) + 100000;
		return String.valueOf(code);
	}

	private boolean trySendOtpEmail(String email, String otp) {
	    try {
	        OkHttpClient client = new OkHttpClient();

	        String json = "{"
	                + "\"from\":\"onboarding@resend.dev\","
	                + "\"to\":[\"" + email + "\"],"
	                + "\"subject\":\"Your Career Assessment OTP\","
	                + "\"html\":\"<h2>Your OTP is: " + otp + "</h2><p>Expires in 10 minutes.</p>\""
	                + "}";

	        Request request = new Request.Builder()
	                .url("https://api.resend.com/emails")
	                .post(RequestBody.create(json, MediaType.parse("application/json")))
	                .addHeader("Authorization", "Bearer " + System.getenv("RESEND_API_KEY"))
	                .addHeader("Content-Type", "application/json")
	                .build();

	        Response response = client.newCall(request).execute();

	        if (!response.isSuccessful()) {
	            log.warn("Resend email failed: {}", response.body().string());
	            return false;
	        }

	        return true;

	    } catch (Exception e) {
	        log.warn("Unable to send OTP email to {}: {}", email, e.getMessage());
	        return false;
	    }
	}
	private record OtpEntry(String code, Instant expiresAt) {
		private boolean isExpired() {
			return Instant.now().isAfter(expiresAt);
		}
	}
}
