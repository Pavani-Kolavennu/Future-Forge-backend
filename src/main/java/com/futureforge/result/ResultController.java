package com.futureforge.result;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/results", "/results"})
public class ResultController {
	private final ResultService resultService;

	public ResultController(ResultService resultService) {
		this.resultService = resultService;
	}

	@GetMapping
	public List<ResultDto> getAllResults() {
		return resultService.findAll().stream().map(resultService::toDto).toList();
	}

	@GetMapping("/user/{userId}")
	public List<ResultDto> getResultsByUser(@PathVariable Long userId) {
		return resultService.findByUserId(userId).stream().map(resultService::toDto).toList();
	}

	@GetMapping("/{resultId}")
	public ResultDto getResult(@PathVariable Long resultId) {
		return resultService.toDto(resultService.getById(resultId));
	}

	@GetMapping("/history/student/{studentEmail}")
	public List<Map<String, Object>> getHistoryByStudentEmail(@PathVariable String studentEmail) {
		return resultService.findHistoryByUserEmail(studentEmail.trim().toLowerCase()).stream()
				.map(item -> Map.<String, Object>of(
						"userId", item.userEmail == null ? "" : item.userEmail,
						"career", item.career == null ? "Software Developer" : item.career,
						"score", item.score,
						"date", item.createdAt == null ? "" : item.createdAt.toString()
				))
				.toList();
	}

	@PostMapping("/history")
	public ResponseEntity<Map<String, Object>> createHistory(@RequestBody HistoryRequest request) {
		Result created = resultService.createHistory(request.userId(), request.career(), request.score(), request.date());
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
				"userId", created.userEmail,
				"career", created.career,
				"score", created.score,
				"date", created.createdAt.toString()
		));
	}

	public record HistoryRequest(String userId, String career, Integer score, String date) {
	}
}
