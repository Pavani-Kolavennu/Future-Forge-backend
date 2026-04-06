package com.futureforge.result;

import java.time.Instant;
import java.util.List;

import com.futureforge.assessment.Submission;
import com.futureforge.common.ResourceNotFoundException;
import com.futureforge.common.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResultService {
	private final ResultRepository resultRepository;

	public ResultService(ResultRepository resultRepository) {
		this.resultRepository = resultRepository;
	}

	public List<Result> findAll() {
		return resultRepository.findAll();
	}

	public List<Result> findByUserId(Long userId) {
		return resultRepository.findByUserId(userId);
	}

	public Result getById(Long resultId) {
		return resultRepository.findById(resultId)
				.orElseThrow(() -> new ResourceNotFoundException("Result not found"));
	}

	public Result recordResult(Submission submission) {
		Result result = resultRepository.findBySubmissionId(submission.id).orElseGet(Result::new);
		result.userId = submission.userId;
		result.userEmail = submission.studentEmail;
		result.assessmentId = submission.assessmentId;
		result.submissionId = submission.id;
		result.score = submission.score;
		result.totalQuestions = submission.totalQuestions;
		result.percentage = submission.percentage;
		result.passed = submission.percentage >= 50.0;
		return resultRepository.save(result);
	}

	public List<Result> findHistoryByUserEmail(String userEmail) {
		return resultRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
	}

	public Result createHistory(String userEmail, String career, Integer score, String date) {
		if (userEmail == null || userEmail.isBlank() || career == null || career.isBlank() || score == null) {
			throw new ValidationException("User id, career and score are required");
		}

		Result result = new Result();
		result.userId = 0L;
		result.userEmail = userEmail.trim().toLowerCase();
		result.assessmentId = 0L;
		result.career = career.trim();
		result.submissionId = null;
		result.score = score;
		result.totalQuestions = 1;
		result.percentage = score;
		result.passed = score >= 50;
		if (date != null && !date.isBlank()) {
			try {
				result.createdAt = Instant.parse(date);
			} catch (Exception ex) {
				throw new ValidationException("Date must be a valid ISO-8601 timestamp");
			}
		}
		return resultRepository.save(result);
	}

	public ResultDto toDto(Result result) {
		return new ResultDto(result.id, result.userId, result.userEmail, result.assessmentId, result.career, result.submissionId, result.score, result.totalQuestions, result.percentage, result.passed, result.createdAt);
	}
}
