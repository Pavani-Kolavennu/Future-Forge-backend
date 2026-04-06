package com.futureforge.question;

import java.util.List;

public record PublicQuestionDto(
		Long id,
		Long assessmentId,
		String text,
		String explanation,
		List<String> options
) {
}