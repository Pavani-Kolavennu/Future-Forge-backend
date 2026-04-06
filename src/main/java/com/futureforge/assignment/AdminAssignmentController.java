package com.futureforge.assignment;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/admin/assignments", "/admin/assignments"})
public class AdminAssignmentController {
	private final AssignmentService assignmentService;

	public AdminAssignmentController(AssignmentService assignmentService) {
		this.assignmentService = assignmentService;
	}

	@GetMapping
	public List<TestAssignment> getAllAssignments() {
		return assignmentService.findAll();
	}

	@GetMapping("/user/{userId}")
	public List<TestAssignment> getAssignmentsByUser(@PathVariable Long userId) {
		return assignmentService.findByStudentId(String.valueOf(userId));
	}

	@GetMapping("/student/{studentId}")
	public List<TestAssignment> getAssignmentsByStudent(@PathVariable String studentId) {
		return assignmentService.findByStudentId(studentId.trim().toLowerCase());
	}

	@PostMapping
	public ResponseEntity<TestAssignment> assignTest(@Valid @RequestBody AssignTestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assign(dto));
	}

	@PutMapping("/{assignmentId}/status")
	public TestAssignment updateStatus(@PathVariable Long assignmentId, @RequestBody String status) {
		return assignmentService.updateStatus(assignmentId, status);
	}

	@DeleteMapping("/{assignmentId}")
	public ResponseEntity<Void> deleteAssignment(@PathVariable Long assignmentId) {
		assignmentService.delete(assignmentId);
		return ResponseEntity.noContent().build();
	}
}

