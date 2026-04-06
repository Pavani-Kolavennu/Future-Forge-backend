package com.futureforge.question;

import java.util.List;

public record UpdateQuestionDto(
		Long assessmentId,
		String text,
		String explanation,
		Boolean active,
		List<String> options,
		Integer correctOptionIndex
) {
}
