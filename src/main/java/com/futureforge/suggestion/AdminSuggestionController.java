package com.futureforge.suggestion;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/admin/suggestions", "/admin/suggestions"})
public class AdminSuggestionController {
	private final SuggestionService suggestionService;

	public AdminSuggestionController(SuggestionService suggestionService) {
		this.suggestionService = suggestionService;
	}

	@GetMapping
	public Map<String, Map<String, Object>> getAllSuggestions() {
		return suggestionService.getAllAsMap();
	}

	@PostMapping
	public Map<String, Object> upsertSuggestion(@RequestBody SuggestionRequest request) {
		return suggestionService.upsert(request.studentId(), request.career(), request.suggestion());
	}

	@DeleteMapping("/{studentId}")
	public ResponseEntity<Void> deleteSuggestion(@PathVariable String studentId) {
		suggestionService.deleteByStudentId(studentId);
		return ResponseEntity.noContent().build();
	}

	public record SuggestionRequest(String studentId, String suggestion, String career) {
	}

}
