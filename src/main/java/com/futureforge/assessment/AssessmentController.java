package com.futureforge.assessment;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.futureforge.result.ResultService;
import com.futureforge.question.PublicQuestionDto;
import com.futureforge.question.QuestionService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {
	private final AssessmentService assessmentService;
	private final ResultService resultService;
	private final QuestionService questionService;

	public AssessmentController(AssessmentService assessmentService, ResultService resultService, QuestionService questionService) {
		this.assessmentService = assessmentService;
		this.resultService = resultService;
		this.questionService = questionService;
	}

	@GetMapping("/{assessmentId}/questions")
	public List<PublicQuestionDto> getAssessmentQuestions(@PathVariable Long assessmentId) {
		return questionService.toPublicQuestions(questionService.findByAssessment(assessmentId));
	}

	@PostMapping("/{assessmentId}/submit")
	public ResponseEntity<com.futureforge.result.ResultDto> submitAssessment(@PathVariable Long assessmentId, @RequestBody Submission submission) {
		Submission savedSubmission = assessmentService.submitAssessment(assessmentId, submission);
		return ResponseEntity.ok(resultService.toDto(resultService.recordResult(savedSubmission)));
	}

	@GetMapping("/submissions")
	@PreAuthorize("hasRole('ADMIN')")
	public Map<String, Map<String, Object>> getAllStudentSubmissions() {
		Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
		assessmentService.findAllSubmissions().forEach(submission -> {
			if (submission.studentEmail == null || submission.studentEmail.isBlank() || submission.assignmentId == null) {
				return;
			}
			Map<String, Object> byAssignment = grouped.computeIfAbsent(submission.studentEmail, key -> new LinkedHashMap<>());
			byAssignment.put(String.valueOf(submission.assignmentId), toStudentSubmissionResponse(submission));
		});
		return grouped;
	}

	@GetMapping("/submissions/student/{studentEmail}")
	@PreAuthorize("hasRole('ADMIN') or #studentEmail.trim().toLowerCase() == authentication.principal.email")
	public Map<String, Map<String, Object>> getStudentSubmissions(@PathVariable String studentEmail) {
		Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
		Map<String, Object> byAssignment = new LinkedHashMap<>();
		assessmentService.findSubmissionsByStudentEmail(studentEmail.trim().toLowerCase()).forEach(submission -> {
			if (submission.assignmentId != null) {
				byAssignment.put(String.valueOf(submission.assignmentId), toStudentSubmissionResponse(submission));
			}
		});
		grouped.put(studentEmail.trim().toLowerCase(), byAssignment);
		return grouped;
	}

	@PostMapping("/submissions")
	@PreAuthorize("hasRole('ADMIN') or #request.studentEmail().trim().toLowerCase() == authentication.principal.email")
	public Map<String, Object> upsertStudentSubmission(@RequestBody StudentSubmissionRequest request) {
		return toStudentSubmissionResponse(
				assessmentService.upsertStudentSubmission(
						request.studentEmail(),
						request.assignmentId(),
						request.answers()
				)
		);
	}

	private Map<String, Object> toStudentSubmissionResponse(Submission submission) {
		Map<String, Integer> answers = new LinkedHashMap<>();
		submission.answers.forEach(answer -> {
			if (answer.questionId != null && answer.selectedOptionIndex != null) {
				answers.put(String.valueOf(answer.questionId), answer.selectedOptionIndex);
			}
		});

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("answers", answers);
		response.put("submittedAt", submission.submittedAt == null ? Instant.now().toString() : submission.submittedAt.toString());
		response.put("assignmentId", submission.assignmentId);
		return response;
	}

	public record StudentSubmissionRequest(String studentEmail, Long assignmentId, Map<String, Integer> answers) {
	}
}
