package com.futureforge.question;

import java.util.ArrayList;
import java.util.List;

import com.futureforge.assessment.AssessmentRepository;
import com.futureforge.assessment.Option;
import com.futureforge.assessment.Question;
import com.futureforge.assessment.QuestionRepository;
import com.futureforge.common.ResourceNotFoundException;
import com.futureforge.common.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuestionService {
	private final QuestionRepository questionRepository;
	private final AssessmentRepository assessmentRepository;

	public QuestionService(QuestionRepository questionRepository, AssessmentRepository assessmentRepository) {
		this.questionRepository = questionRepository;
		this.assessmentRepository = assessmentRepository;
	}

	public List<Question> findAll() {
		return questionRepository.findAll();
	}

	public List<Question> findByAssessment(Long assessmentId) {
		return questionRepository.findByAssessmentId(assessmentId);
	}

	public Question getById(Long questionId) {
		return questionRepository.findById(questionId)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found"));
	}

	public Question create(CreateQuestionDto dto) {
		validateAssessment(dto.assessmentId());
		Question question = new Question();
		apply(question, dto.assessmentId(), dto.text(), dto.explanation(), dto.active(), dto.options(), dto.correctOptionIndex());
		return questionRepository.save(question);
	}

	public Question update(Long questionId, UpdateQuestionDto dto) {
		Question question = getById(questionId);
		Long assessmentId = dto.assessmentId() == null ? question.assessmentId : dto.assessmentId();
		validateAssessment(assessmentId);
		apply(question,
				assessmentId,
				dto.text() == null ? question.text : dto.text(),
				dto.explanation() == null ? question.explanation : dto.explanation(),
				dto.active() == null ? question.active : dto.active(),
				dto.options() == null ? question.options.stream().map(option -> option.text).toList() : dto.options(),
				dto.correctOptionIndex() == null ? question.correctOptionIndex : dto.correctOptionIndex());
		return questionRepository.save(question);
	}

	public void delete(Long questionId) {
		questionRepository.delete(getById(questionId));
	}

	public AdminQuestion toAdminQuestion(Question question) {
		return new AdminQuestion(question.id, question.assessmentId, question.text, question.explanation, question.active, question.options, question.correctOptionIndex);
	}

	public List<AdminQuestion> toAdminQuestions(List<Question> questions) {
		return questions.stream().map(this::toAdminQuestion).toList();
	}

	public List<SimpleQuestionDto> toSimpleQuestions(List<Question> questions) {
		return questions.stream().map(this::toSimpleQuestion).toList();
	}

	public SimpleQuestionDto toSimpleQuestion(Question question) {
		return new SimpleQuestionDto(
				question.id,
				question.text,
				question.options.stream().map(option -> option.text).toList()
		);
	}

	public Question createSimpleQuestion(String text, List<String> options) {
		if (text == null || text.isBlank()) {
			throw new ValidationException("Question text is required");
		}
		Question question = new Question();
		apply(question, 0L, text, null, true, options, 0);
		return questionRepository.save(question);
	}

	public PublicQuestionDto toPublicQuestion(Question question) {
		return new PublicQuestionDto(
				question.id,
				question.assessmentId,
				question.text,
				question.explanation,
				question.options.stream().map(option -> option.text).toList());
	}

	public List<PublicQuestionDto> toPublicQuestions(List<Question> questions) {
		return questions.stream().map(this::toPublicQuestion).toList();
	}

	private void validateAssessment(Long assessmentId) {
		if (assessmentId == null || !assessmentRepository.existsById(assessmentId)) {
			throw new ValidationException("Assessment not found");
		}
	}

	private void apply(Question question, Long assessmentId, String text, String explanation, Boolean active, List<String> options, Integer correctOptionIndex) {
		if (options == null || options.size() < 2) {
			throw new ValidationException("At least two options are required");
		}
		if (correctOptionIndex == null || correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
			throw new ValidationException("Correct option index is invalid");
		}

		question.assessmentId = assessmentId;
		question.text = text;
		question.explanation = explanation;
		question.active = active == null || active;
		question.correctOptionIndex = correctOptionIndex;
		question.options = new ArrayList<>(options.stream()
				.map(optionText -> new Option(optionText, false))
				.toList());
		question.options.set(correctOptionIndex, new Option(options.get(correctOptionIndex), true));
	}

	public record SimpleQuestionDto(Long id, String text, List<String> options) {
	}
}
