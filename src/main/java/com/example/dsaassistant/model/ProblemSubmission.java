package com.example.dsaassistant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "Problem is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    
    @NotBlank(message = "Code is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.PENDING;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    @Column(name = "memory_used_mb")
    private Double memoryUsedMB;
    
    @Column(name = "test_cases_passed")
    private Integer testCasesPassed = 0;
    
    @Column(name = "total_test_cases")
    private Integer totalTestCases = 0;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;
    
    public enum Language {
        JAVA, PYTHON, CPP, JAVASCRIPT
    }
    
    public enum SubmissionStatus {
        PENDING, RUNNING, ACCEPTED, WRONG_ANSWER, TIME_LIMIT_EXCEEDED, 
        MEMORY_LIMIT_EXCEEDED, RUNTIME_ERROR, COMPILATION_ERROR
    }
}
