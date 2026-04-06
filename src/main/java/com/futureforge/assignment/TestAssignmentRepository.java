package com.futureforge.assignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestAssignmentRepository extends JpaRepository<TestAssignment, Long> {

	List<TestAssignment> findByStudentId(String studentId);
}

