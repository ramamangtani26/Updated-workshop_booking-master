package com.example.dsaassistant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Progress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressType type;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "difficulty")
    private String difficulty;
    
    @Column(name = "problems_solved")
    private Integer problemsSolved = 0;
    
    @Column(name = "total_problems")
    private Integer totalProblems = 0;
    
    @Column(name = "accuracy_percentage")
    private Double accuracyPercentage = 0.0;
    
    @Column(name = "average_time_ms")
    private Long averageTimeMs = 0L;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ProgressType {
        OVERALL, CATEGORY, DIFFICULTY
    }
}
