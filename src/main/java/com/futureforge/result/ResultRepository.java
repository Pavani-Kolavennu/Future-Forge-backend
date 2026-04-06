package com.futureforge.result;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {

	List<Result> findByUserId(Long userId);

	List<Result> findByUserEmailOrderByCreatedAtDesc(String userEmail);

	Optional<Result> findBySubmissionId(Long submissionId);
}
