package com.futureforge.assessment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final TestAssignmentRepository assignmentRepository;

    public AssessmentService(
            AssessmentRepository assessmentRepository,
            QuestionRepository questionRepository,
            SubmissionRepository submissionRepository,
            TestAssignmentRepository assignmentRepository) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<Assessment> findAll() {
        return assessmentRepository.findAll();
    }

    public List<Assessment> findActive() {
        return assessmentRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public Assessment getById(Long assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
    }

    public Assessment create(Assessment assessment) {
        assessment.id = null;
        return assessmentRepository.save(assessment);
    }

    public Assessment update(Long assessmentId, Assessment update) {
        Assessment assessment = getById(assessmentId);
        assessment.title = update.title;
        assessment.description = update.description;
        assessment.durationMinutes = update.durationMinutes;
        assessment.passingScore = update.passingScore;
        assessment.active = update.active;
        return assessmentRepository.save(assessment);
    }

    public void delete(Long assessmentId) {
        assessmentRepository.delete(getById(assessmentId));
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

        TestAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        List<Long> questionIds = answers.keySet().stream()
                .map(Long::parseLong)
                .toList();

        Map<Long, Question> questionMap = questionRepository.findAllById(questionIds).stream()
                .collect(Collectors.toMap(q -> q.id, q -> q));

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
                    Long qId = Long.parseLong(entry.getKey());
                    Integer idx = entry.getValue();

                    SubmissionAnswer answer = new SubmissionAnswer();
                    answer.questionId = qId;
                    answer.selectedOptionIndex = idx;
                    answer.correct = false;

                    Question q = questionMap.get(qId);
                    if (q != null && idx >= 0 && idx < q.options.size()) {
                        Option selected = q.options.get(idx);
                        answer.selectedOptionText = selected.text;
                        answer.correct = selected.correct;
                    }

                    return answer;
                })
                .toList();

        submission.totalQuestions = submission.answers.size();
        submission.score = (int) submission.answers.stream().filter(a -> a.correct).count();
        submission.percentage = submission.totalQuestions == 0 ? 0.0 : (submission.score * 100.0) / submission.totalQuestions;

        return submissionRepository.save(submission);
    }
}