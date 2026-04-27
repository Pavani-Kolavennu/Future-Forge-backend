package com.futureforge.question;

import java.util.List;

import com.futureforge.assessment.Option;

public record AdminQuestion(
		Long id,
		Long assessmentId,
		String text,
		boolean active,
		List<Option> options
) {
}
