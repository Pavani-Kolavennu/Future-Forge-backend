package com.futureforge.assessment;

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
@Table(name = "assessments")
public class Assessment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(nullable = false)
	public String title;


	public Integer durationMinutes;

	public Integer passingScore;

	@Column(nullable = false)
	public boolean active = true;

	@Column(nullable = false, updatable = false)
	public Instant createdAt;

	@Column(nullable = false)
	public Instant updatedAt;

	public Assessment() {
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
