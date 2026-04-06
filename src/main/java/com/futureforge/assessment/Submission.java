package com.futureforge.assessment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "submissions")
public class Submission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	public String studentEmail;

	public Long assignmentId;

	@Column(nullable = false)
	public Long userId;

	@Column(nullable = false)
	public Long assessmentId;

	@Column(nullable = false)
	public int score;

	@Column(nullable = false)
	public int totalQuestions;

	@Column(nullable = false)
	public double percentage;

	@Column(nullable = false)
	public Instant submittedAt;

	@ElementCollection
	@CollectionTable(name = "submission_answers", joinColumns = @JoinColumn(name = "submission_id"))
	public List<SubmissionAnswer> answers = new ArrayList<>();

	public Submission() {
	}

	@PrePersist
	public void onCreate() {
		if (this.submittedAt == null) {
			this.submittedAt = Instant.now();
		}
	}
}
