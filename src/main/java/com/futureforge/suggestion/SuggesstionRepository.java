package com.futureforge.suggestion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggesstionRepository extends JpaRepository<PersonalizedSuggestion, Long> {
	Optional<PersonalizedSuggestion> findByStudentId(String studentId);

	void deleteByStudentId(String studentId);

}
