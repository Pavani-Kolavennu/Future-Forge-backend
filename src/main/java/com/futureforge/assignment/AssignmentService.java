package com.futureforge.assignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.futureforge.assessment.AssessmentRepository;
import com.futureforge.common.ResourceNotFoundException;
import com.futureforge.common.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssignmentService {
	private final TestAssignmentRepository assignmentRepository;
	private final AssessmentRepository assessmentRepository;

	public AssignmentService(TestAssignmentRepository assignmentRepository, AssessmentRepository assessmentRepository) {
		this.assignmentRepository = assignmentRepository;
		this.assessmentRepository = assessmentRepository;
	}

	public List<TestAssignment> findAll() {
		return assignmentRepository.findAll();
	}

	public List<TestAssignment> findByStudentId(String studentId) {
		return assignmentRepository.findByStudentId(studentId.trim().toLowerCase());
	}

	public TestAssignment getById(Long assignmentId) {
		return assignmentRepository.findById(Objects.requireNonNull(assignmentId, "assignmentId is required"))
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
	}

	public TestAssignment assign(AssignTestDto dto) {
		if (dto.questions() == null || dto.questions().isEmpty()) {
			throw new ValidationException("At least one question is required");
		}
		Long assessmentId = dto.assessmentId();
		if (assessmentId == null || !assessmentRepository.existsById(assessmentId)) {
			throw new ValidationException("Assessment not found");
		}

		LocalDate parsedDueDate;
		try {
			parsedDueDate = LocalDate.parse(dto.dueDate());
		} catch (Exception ex) {
			throw new ValidationException("Due date must be in yyyy-MM-dd format");
		}

		TestAssignment assignment = new TestAssignment();
		assignment.studentId = dto.studentId().trim().toLowerCase();
		assignment.assessmentId = assessmentId;
		assignment.questions = dto.questions();
		assignment.dueDate = parsedDueDate;
		assignment.status = dto.status() == null || dto.status().isBlank() ? "assigned" : dto.status();
		return assignmentRepository.save(assignment);
	}

	public TestAssignment updateStatus(Long assignmentId, String status) {
		TestAssignment assignment = getById(Objects.requireNonNull(assignmentId, "assignmentId is required"));
		if (status == null || status.isBlank()) {
			throw new ValidationException("Status is required");
		}
		assignment.status = status;
		return assignmentRepository.save(assignment);
	}

	public void delete(Long assignmentId) {
		assignmentRepository.delete(Objects.requireNonNull(getById(assignmentId), "assignment is required"));
	}
}
