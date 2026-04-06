package com.futureforge.suggestion;

import java.util.LinkedHashMap;
import java.util.Map;

import com.futureforge.common.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SuggestionService {
	private final SuggesstionRepository suggestionRepository;

	public SuggestionService(SuggesstionRepository suggestionRepository) {
		this.suggestionRepository = suggestionRepository;
	}

	public Map<String, Map<String, Object>> getAllAsMap() {
		Map<String, Map<String, Object>> data = new LinkedHashMap<>();
		suggestionRepository.findAll().forEach(item -> {
			Map<String, Object> value = new LinkedHashMap<>();
			value.put("career", item.career);
			value.put("suggestion", item.suggestion);
			value.put("date", item.date.toString());
			data.put(item.studentId, value);
		});
		return data;
	}

	public Map<String, Object> upsert(String studentId, String career, String suggestionText) {
		if (studentId == null || studentId.isBlank() || career == null || career.isBlank() || suggestionText == null || suggestionText.isBlank()) {
			throw new ValidationException("Student id, career and suggestion are required");
		}

		PersonalizedSuggestion item = suggestionRepository.findByStudentId(studentId.trim().toLowerCase())
				.orElseGet(PersonalizedSuggestion::new);
		item.studentId = studentId.trim().toLowerCase();
		item.career = career.trim();
		item.suggestion = suggestionText.trim();
		PersonalizedSuggestion saved = suggestionRepository.save(item);

		Map<String, Object> value = new LinkedHashMap<>();
		value.put("studentId", saved.studentId);
		value.put("career", saved.career);
		value.put("suggestion", saved.suggestion);
		value.put("date", saved.date.toString());
		return value;
	}

	public void deleteByStudentId(String studentId) {
		suggestionRepository.deleteByStudentId(studentId.trim().toLowerCase());
	}

}
