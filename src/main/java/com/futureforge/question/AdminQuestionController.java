package com.futureforge.question;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {

    private final QuestionService questionService;

    public AdminQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

   
    @GetMapping
    public List<AdminQuestion> getAllQuestions() {
        return questionService.toAdminQuestions(questionService.findAll());
    }

	@GetMapping("/simple")
	public List<QuestionService.SimpleQuestionDto> getSimpleQuestions() {
		return questionService.toSimpleQuestions(questionService.findAll());
	}

	@GetMapping("/{questionId:\\d+}")
	public AdminQuestion getQuestion(@PathVariable Long questionId) {
		return questionService.toAdminQuestion(questionService.getById(questionId));
	}

	@GetMapping("/assessment/{assessmentId}")
	public List<AdminQuestion> getQuestionsByAssessment(@PathVariable Long assessmentId) {
		return questionService.toAdminQuestions(questionService.findByAssessment(assessmentId));
	}

	@PostMapping
	public ResponseEntity<AdminQuestion> createQuestion(@Valid @RequestBody CreateQuestionDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(questionService.toAdminQuestion(questionService.create(dto)));
	}

	@PostMapping("/simple")
	public ResponseEntity<QuestionService.SimpleQuestionDto> createSimpleQuestion(@RequestBody SimpleCreateQuestionRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(questionService.toSimpleQuestion(questionService.createSimpleQuestion(request.text(), request.options())));
	}

	@PutMapping("/{questionId:\\d+}")
	public AdminQuestion updateQuestion(@PathVariable Long questionId, @RequestBody UpdateQuestionDto dto) {
		return questionService.toAdminQuestion(questionService.update(questionId, dto));
	}

	@DeleteMapping("/{questionId:\\d+}")
	public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
		questionService.delete(questionId);
		return ResponseEntity.noContent().build();
	}

	public record SimpleCreateQuestionRequest(String text, List<String> options) {
	}
}