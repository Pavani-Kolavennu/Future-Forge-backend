package com.futureforge.suggestion;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suggestions")
public class StudentSuggestionController {

	private final SuggestionService suggestionService;

	public StudentSuggestionController(SuggestionService suggestionService) {
		this.suggestionService = suggestionService;
	}

	@GetMapping("/student/{studentEmail}")
	@PreAuthorize("hasRole('ADMIN') or #studentEmail.trim().toLowerCase() == authentication.principal.email")
	public Map<String, Object> getSuggestionForStudent(@PathVariable String studentEmail) {
		return suggestionService.findByStudentId(studentEmail).orElse(Map.of());
	}
}