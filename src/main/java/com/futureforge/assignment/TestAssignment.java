package com.futureforge.assignment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_assignments")
public class TestAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "student_id", nullable = false)
    public String studentId;

    @Column(name = "assessment_id", nullable = false)
    public Long assessmentId;

    @ElementCollection
    @CollectionTable(name = "test_assignment_questions", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "question_id", nullable = false)
    public List<Long> questions = new ArrayList<>();

    @Column(name = "due_date", nullable = false)
    public LocalDate dueDate;

    @Column(nullable = false)
    public String status = "assigned";

    @Column(name = "assigned_date", nullable = false, updatable = false)
    public LocalDateTime assignedDate;

    @PrePersist
    public void onCreate() {
        if (assignedDate == null) {
            assignedDate = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "assigned";
        }
    }
}