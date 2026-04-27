package com.futureforge.question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
		return questionRepository.findById(Objects.requireNonNull(questionId, "questionId is required"))
				.orElseThrow(() -> new ResourceNotFoundException("Question not found"));
	}

	public Question create(CreateQuestionDto dto) {
		validateAssessment(dto.assessmentId());
		Question question = new Question();
		apply(question, dto.assessmentId(), dto.text(), dto.active(), dto.options());
		return questionRepository.save(Objects.requireNonNull(question, "question is required"));
	}

	public Question update(Long questionId, UpdateQuestionDto dto) {
		Question question = getById(questionId);
		Long assessmentId = dto.assessmentId() == null ? question.assessmentId : dto.assessmentId();
		validateAssessment(assessmentId);
		apply(question,
				assessmentId,
				dto.text() == null ? question.text : dto.text(),
				dto.active() == null ? question.active : dto.active(),
				dto.options() == null ? question.options.stream().map(option -> option.text).toList() : dto.options());
		return questionRepository.save(Objects.requireNonNull(question, "question is required"));
	}

	public void delete(Long questionId) {
		questionRepository.delete(Objects.requireNonNull(getById(questionId), "question is required"));
	}

	public AdminQuestion toAdminQuestion(Question question) {
		return new AdminQuestion(question.id, question.assessmentId, question.text, question.active, question.options);
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
		apply(question, 0L, text, true, options);
		return questionRepository.save(question);
	}

	public PublicQuestionDto toPublicQuestion(Question question) {
		return new PublicQuestionDto(
				question.id,
				question.assessmentId,
				question.text,
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

	private void apply(Question question, Long assessmentId, String text, Boolean active, List<String> options) {
		if (options == null || options.size() < 2) {
			throw new ValidationException("At least two options are required");
		}

		question.assessmentId = assessmentId;
		question.text = text;
		question.active = active == null || active;
		question.options = new ArrayList<>(options.stream()
				.map(Option::new)
				.toList());
	}

	public record SimpleQuestionDto(Long id, String text, List<String> options) {
	}
}
