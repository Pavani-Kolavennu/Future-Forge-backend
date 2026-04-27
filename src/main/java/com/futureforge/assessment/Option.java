package com.futureforge.assessment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Option {
	@Column(nullable = false)
	public String text;

	public Option() {
	}

	public Option(String text) {
		this.text = text;
	}
}

