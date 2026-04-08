package com.futureforge.user;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(nullable = false)
	public String fullName;

	@Column(nullable = false, unique = true)
	public String email;

	@Column(nullable = false)
	public String password;

	@Column
	public String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Role role = Role.CANDIDATE;

	@Column(nullable = false)
	public boolean enabled = true;

	@Column(nullable = false, updatable = false)
	public Instant createdAt;

	@Column(nullable = false)
	public Instant updatedAt;

	public User() {
	}

	public User(String fullName, String email, String password, String phone, Role role) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.role = role == null ? Role.CANDIDATE : role;
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
