package com.futureforge.question;

import java.util.List;

import com.futureforge.assessment.Option;

public record AdminQuestion(
		Long id,
		Long assessmentId,
		String text,
		String explanation,
		boolean active,
		List<Option> options,
		Integer correctOptionIndex
) {
}
