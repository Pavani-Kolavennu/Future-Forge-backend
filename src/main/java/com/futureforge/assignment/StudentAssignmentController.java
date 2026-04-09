package com.futureforge.assignment;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assignments")
public class StudentAssignmentController {

	private final AssignmentService assignmentService;

	public StudentAssignmentController(AssignmentService assignmentService) {
		this.assignmentService = assignmentService;
	}

	@GetMapping("/student/{studentEmail}")
	@PreAuthorize("hasRole('ADMIN') or #studentEmail.trim().toLowerCase() == authentication.principal.email")
	public List<TestAssignment> getAssignmentsForStudent(@PathVariable String studentEmail) {
		return assignmentService.findByStudentId(studentEmail);
	}
}