package com.futureforge.question;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateQuestionDto(
		@NotNull(message = "Assessment id is required")
		Long assessmentId,
		@NotBlank(message = "Question text is required")
		String text,
		String explanation,
		Boolean active,
		@NotNull(message = "Options are required")
		@Size(min = 2, message = "At least two options are required")
		List<@NotBlank(message = "Option text is required") String> options,
		@NotNull(message = "Correct option index is required")
		Integer correctOptionIndex
) {
}

