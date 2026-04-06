package com.futureforge.assessment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	List<Question> findByAssessmentId(Long assessmentId);
}
