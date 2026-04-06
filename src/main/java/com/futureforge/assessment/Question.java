package com.futureforge.assessment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(nullable = false)
	public Long assessmentId;

	@Column(nullable = false, length = 2000)
	public String text;

	@Column(length = 4000)
	public String explanation;

	@Column(nullable = false)
	public boolean active = true;

	@Column(nullable = false)
	public Integer correctOptionIndex;

	@ElementCollection
	@CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
	public List<Option> options = new ArrayList<>();

	@Column(nullable = false, updatable = false)
	public Instant createdAt;

	@Column(nullable = false)
	public Instant updatedAt;

	public Question() {
	}

	@PrePersist
	public void onCreate() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedAt = Instant.now();
	}
}
