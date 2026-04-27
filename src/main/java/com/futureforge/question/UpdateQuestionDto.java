package com.futureforge.question;

import java.util.List;

public record UpdateQuestionDto(
		Long assessmentId,
		String text,
		Boolean active,
		List<String> options
) {
}
