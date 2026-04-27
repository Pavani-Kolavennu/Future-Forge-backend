package com.futureforge.assessment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SubmissionAnswer {
	@Column(nullable = false)
	public Long questionId;

	public Integer selectedOptionIndex;

	public SubmissionAnswer() {
	}
}
