package com.futureforge.assessment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

	List<Submission> findByUserId(Long userId);

	List<Submission> findByStudentEmail(String studentEmail);

	Optional<Submission> findByStudentEmailAndAssignmentId(String studentEmail, Long assignmentId);
}
