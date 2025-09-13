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
@Table(name = "test_cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Problem is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    
    @NotBlank(message = "Input is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String input;
    
    @NotBlank(message = "Expected output is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;
    
    @Column(name = "is_sample")
    private Boolean isSample = false;
    
    @Column(name = "is_hidden")
    private Boolean isHidden = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
