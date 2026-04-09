package com.futureforge.assessment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	private final QuestionRepository questionRepository;
	private final SubmissionRepository submissionRepository;
	private final TestAssignmentRepository testAssignmentRepository;

	public AssessmentService(AssessmentRepository assessmentRepository, QuestionRepository questionRepository, SubmissionRepository submissionRepository, TestAssignmentRepository testAssignmentRepository) {
		this.assessmentRepository = assessmentRepository;
		this.questionRepository = questionRepository;
		this.submissionRepository = submissionRepository;
		this.testAssignmentRepository = testAssignmentRepository;
	}

	public Assessment getById(Long assessmentId) {
		return assessmentRepository.findById(assessmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
	}

	public Submission submitAssessment(Long assessmentId, Submission submission) {
		Assessment assessment = getById(assessmentId);
		List<Question> questions = questionRepository.findByAssessmentId(assessment.id);
		if (questions.isEmpty()) {
			throw new ValidationException("Assessment has no questions");
		}
		if (submission.answers == null) {
			submission.answers = new ArrayList<>();
		}

		submission.id = null;
		submission.assessmentId = assessment.id;
		submission.submittedAt = Instant.now();
		submission.totalQuestions = questions.size();

		int score = 0;
		for (SubmissionAnswer answer : submission.answers) {
			Question question = questions.stream()
					.filter(item -> item.id.equals(answer.questionId))
					.findFirst()
					.orElse(null);
			if (question == null) {
				continue;
			}

			boolean correct = false;
			if (answer.selectedOptionIndex != null && answer.selectedOptionIndex >= 0 && answer.selectedOptionIndex < question.options.size()) {
				Option selected = question.options.get(answer.selectedOptionIndex);
				correct = selected.correct;
				answer.selectedOptionText = selected.text;
			} else if (answer.selectedOptionText != null) {
				correct = question.options.stream().anyMatch(option -> option.correct && answer.selectedOptionText.equalsIgnoreCase(option.text));
			}
			answer.correct = correct;
			if (correct) {
				score++;
			}
		}

		submission.score = score;
		submission.percentage = questions.isEmpty() ? 0.0 : (score * 100.0) / questions.size();
		return submissionRepository.save(submission);
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
		submission.totalQuestions = answers.size();
		submission.score = 0;
		submission.percentage = 0.0;
		submission.submittedAt = Instant.now();
		submission.answers = answers.entrySet().stream()
				.filter(entry -> entry.getValue() != null)
				.map(entry -> {
					SubmissionAnswer answer = new SubmissionAnswer();
					answer.questionId = Long.parseLong(entry.getKey());
					answer.selectedOptionIndex = entry.getValue();
					answer.correct = false;
					return answer;
				})
				.toList();

		return submissionRepository.save(submission);
	}
}
