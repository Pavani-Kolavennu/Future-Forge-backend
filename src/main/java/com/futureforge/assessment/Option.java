package com.futureforge.assessment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Option {
	@Column(nullable = false)
	public String text;

	@Column(nullable = false)
	public boolean correct;

	public Option() {
	}

	public Option(String text, boolean correct) {
		this.text = text;
		this.correct = correct;
	}
}

