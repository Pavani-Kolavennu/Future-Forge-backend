package com.futureforge.announcement;

import jakarta.validation.constraints.NotBlank;

public record CreateAnnouncementDto(
		@NotBlank(message = "Title is required")
		String title,
		@NotBlank(message = "Content is required")
		String content,
		Boolean active
) {
}

