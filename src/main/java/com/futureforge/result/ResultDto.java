package com.futureforge.result;

import java.time.Instant;

public record ResultDto(
		Long id,
		Long userId,
		String userEmail,
		Long assessmentId,
		String career,
		Long submissionId,
		int score,
		int totalQuestions,
		double percentage,
		boolean passed,
		Instant createdAt
) {
}
