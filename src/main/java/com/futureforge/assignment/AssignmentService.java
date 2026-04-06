package com.futureforge.assignment;

import java.time.LocalDate;
import java.util.List;

import com.futureforge.common.ResourceNotFoundException;
import com.futureforge.common.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssignmentService {
    private final TestAssignmentRepository assignmentRepository;

    public AssignmentService(TestAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public List<TestAssignment> findAll() {
        return assignmentRepository.findAll();
    }

    public List<TestAssignment> findByStudentId(String studentId) {
        return assignmentRepository.findByStudentId(studentId);
    }

    public TestAssignment getById(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
    }

    public TestAssignment assign(AssignTestDto dto) {
        if (dto.questions() == null || dto.questions().isEmpty()) {
            throw new ValidationException("At least one question is required");
        }
        if (dto.assessmentId() == null || dto.assessmentId() <= 0) {
            throw new ValidationException("Assessment id is required");
        }

        LocalDate parsedDueDate;
        try {
            parsedDueDate = LocalDate.parse(dto.dueDate());
        } catch (Exception ex) {
            throw new ValidationException("Due date must be in yyyy-MM-dd format");
        }

        TestAssignment assignment = new TestAssignment();
        assignment.studentId = dto.studentId().trim().toLowerCase();
        assignment.assessmentId = dto.assessmentId();
        assignment.questions = dto.questions();
        assignment.dueDate = parsedDueDate;
        assignment.status = (dto.status() == null || dto.status().isBlank()) ? "assigned" : dto.status();

        return assignmentRepository.save(assignment);
    }

    public TestAssignment updateStatus(Long assignmentId, String status) {
        TestAssignment assignment = getById(assignmentId);
        if (status == null || status.isBlank()) {
            throw new ValidationException("Status is required");
        }
        assignment.status = status;
        return assignmentRepository.save(assignment);
    }

    public void delete(Long assignmentId) {
        assignmentRepository.delete(getById(assignmentId));
    }
}