package com.futureforge.result;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "results")
public class Result {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(nullable = false)
	public Long userId;

	public String userEmail;

	@Column(nullable = false)
	public Long assessmentId;

	public String career;

	public Long submissionId;

	@Column(nullable = false)
	public int score;

	@Column(nullable = false)
	public int totalQuestions;

	@Column(nullable = false)
	public double percentage;

	@Column(nullable = false)
	public boolean passed;

	@Column(nullable = false, updatable = false)
	public Instant createdAt;

	public Result() {
	}

	@PrePersist
	public void onCreate() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
