package com.futureforge.suggestion;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "personalized_suggestions")
public class PersonalizedSuggestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(nullable = false, unique = true)
	public String studentId;

	@Column(nullable = false)
	public String career;

	@Column(nullable = false, length = 5000)
	public String suggestion;

	@Column(nullable = false)
	public Instant date;

	@PrePersist
	public void onCreate() {
		if (date == null) {
			date = Instant.now();
		}
	}

	@PreUpdate
	public void onUpdate() {
		date = Instant.now();
	}

}
