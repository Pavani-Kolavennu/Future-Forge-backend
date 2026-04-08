package com.futureforge.assignment;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AssignTestDto(
		@NotBlank(message = "Student id is required")
		String studentId,
		@NotNull(message = "Assessment id is required")
		Long assessmentId,
		@NotEmpty(message = "At least one question is required")
		List<Long> questions,
		@NotNull(message = "Due date is required")
		String dueDate,
		String status
) {
}
