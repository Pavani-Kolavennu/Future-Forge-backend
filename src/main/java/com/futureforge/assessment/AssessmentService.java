package com.futureforge.assessment;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.futureforge.assignment.TestAssignment;
import com.futureforge.assignment.TestAssignmentRepository;
import com.futureforge.common.ResourceNotFoundException;
import com.futureforge.common.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssessmentService {
	private final AssessmentRepository assessmentRepository;
	private final SubmissionRepository submissionRepository;
	private final TestAssignmentRepository testAssignmentRepository;

	public AssessmentService(AssessmentRepository assessmentRepository, SubmissionRepository submissionRepository, TestAssignmentRepository testAssignmentRepository) {
		this.assessmentRepository = assessmentRepository;
		this.submissionRepository = submissionRepository;
		this.testAssignmentRepository = testAssignmentRepository;
	}

	public Assessment getById(Long assessmentId) {
		return assessmentRepository.findById(Objects.requireNonNull(assessmentId, "assessmentId is required"))
				.orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
	}

	public List<Assessment> findAll() {
		return assessmentRepository.findAll();
	}

	public Assessment createAssessment(String title, Boolean active) {
		if (title == null || title.isBlank()) {
			throw new ValidationException("Assessment title is required");
		}

		Assessment assessment = new Assessment();
		assessment.title = title.trim();
		assessment.active = active == null || active;
		return assessmentRepository.save(assessment);
	}

	public List<Submission> findAllSubmissions() {
		return submissionRepository.findAll();
	}

	public List<Submission> findSubmissionsByStudentEmail(String studentEmail) {
		return submissionRepository.findByStudentEmail(studentEmail);
	}

	public Submission upsertStudentSubmission(String studentEmail, Long assignmentId, Map<String, Integer> answers) {
		if (studentEmail == null || studentEmail.isBlank() || assignmentId == null || answers == null || answers.isEmpty()) {
			throw new ValidationException("Student email, assignment id, and answers are required");
		}
		TestAssignment assignment = testAssignmentRepository.findById(assignmentId)
				.orElseThrow(() -> new ValidationException("Assignment not found"));
		if (assignment.assessmentId == null) {
			throw new ValidationException("Assigned test is missing assessment id");
		}

		Submission submission = submissionRepository.findByStudentEmailAndAssignmentId(studentEmail.trim().toLowerCase(), assignmentId)
				.orElseGet(Submission::new);
		submission.studentEmail = studentEmail.trim().toLowerCase();
		submission.assignmentId = assignmentId;
		submission.userId = 0L;
		submission.assessmentId = assignment.assessmentId;
		submission.submittedAt = Instant.now();
		submission.answers = answers.entrySet().stream()
				.filter(entry -> entry.getValue() != null)
				.map(entry -> {
					SubmissionAnswer answer = new SubmissionAnswer();
					answer.questionId = Long.parseLong(entry.getKey());
					answer.selectedOptionIndex = entry.getValue();
					return answer;
				})
				.toList();

		return submissionRepository.save(submission);
	}
}
