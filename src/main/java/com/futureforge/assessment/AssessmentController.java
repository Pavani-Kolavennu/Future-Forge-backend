package com.futureforge.assessment;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.futureforge.question.PublicQuestionDto;
import com.futureforge.question.QuestionService;
import com.futureforge.result.ResultDto;
import com.futureforge.result.ResultService;

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

    public AssessmentController(
            AssessmentService assessmentService,
            ResultService resultService,
            QuestionService questionService) {
        this.assessmentService = assessmentService;
        this.resultService = resultService;
        this.questionService = questionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Assessment> getAllAssessments() {
        return assessmentService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Assessment createAssessment(@Valid @RequestBody AssessmentRequest request) {
        return assessmentService.createAssessment(
                request.title(),
                request.durationMinutes(),
                request.passingScore(),
                request.active()
        );
    }

    @GetMapping("/{assessmentId}/questions")
    public List<PublicQuestionDto> getAssessmentQuestions(@PathVariable Long assessmentId) {
        return questionService.toPublicQuestions(questionService.findByAssessment(assessmentId));
    }

    @PostMapping("/{assessmentId}/submit")
    public ResponseEntity<ResultDto> submitAssessment(
            @PathVariable Long assessmentId,
            @Valid @RequestBody Submission submission) {
        Submission savedSubmission = assessmentService.submitAssessment(assessmentId, submission);
        return ResponseEntity.ok(resultService.toDto(resultService.recordResult(savedSubmission)));
    }

    @GetMapping("/submissions")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Map<String, Object>> getAllStudentSubmissions() {
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();

        assessmentService.findAllSubmissions().forEach(submission -> {
            String studentEmail = normalizeEmail(submission.studentEmail);
            if (studentEmail == null || submission.assignmentId == null) {
                return;
            }

            grouped.computeIfAbsent(studentEmail, key -> new LinkedHashMap<>())
                    .put(String.valueOf(submission.assignmentId), toStudentSubmissionResponse(submission));
        });

        return grouped;
    }

    @GetMapping("/submissions/student/{studentEmail}")
    @PreAuthorize("hasRole('ADMIN') or #studentEmail != null and authentication.principal.email != null and #studentEmail.trim().equalsIgnoreCase(authentication.principal.email.trim())")
    public Map<String, Map<String, Object>> getStudentSubmissions(@PathVariable String studentEmail) {
        String normalizedEmail = normalizeEmail(studentEmail);
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        Map<String, Object> byAssignment = new LinkedHashMap<>();

        assessmentService.findSubmissionsByStudentEmail(normalizedEmail).forEach(submission -> {
            if (submission.assignmentId != null) {
                byAssignment.put(String.valueOf(submission.assignmentId), toStudentSubmissionResponse(submission));
            }
        });

        grouped.put(normalizedEmail, byAssignment);
        return grouped;
    }

    @PostMapping("/submissions")
    @PreAuthorize("hasRole('ADMIN') or #request.studentEmail != null and authentication.principal.email != null and #request.studentEmail.trim().equalsIgnoreCase(authentication.principal.email.trim())")
    public Map<String, Object> upsertStudentSubmission(@Valid @RequestBody StudentSubmissionRequest request) {
        return toStudentSubmissionResponse(
                assessmentService.upsertStudentSubmission(
                        normalizeEmail(request.studentEmail()),
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
        response.put("studentEmail", normalizeEmail(submission.studentEmail));
        return response;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public record StudentSubmissionRequest(String studentEmail, Long assignmentId, Map<String, Integer> answers) {
    }

    public record AssessmentRequest(String title, Integer durationMinutes, Integer passingScore, Boolean active) {
    }
}