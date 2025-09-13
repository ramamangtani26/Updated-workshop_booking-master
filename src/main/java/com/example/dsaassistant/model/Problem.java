package com.example.dsaassistant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String problemStatement;
    
    @Column(columnDefinition = "TEXT")
    private String constraints;
    
    @Column(columnDefinition = "TEXT")
    private String examples;
    
    @Column(columnDefinition = "TEXT")
    private String expectedOutput;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    
    @ElementCollection
    @CollectionTable(name = "problem_tags", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "tag")
    private List<String> tags;
    
    @Positive(message = "Time limit must be positive")
    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds = 1;
    
    @Positive(message = "Memory limit must be positive")
    @Column(name = "memory_limit_mb")
    private Integer memoryLimitMB = 256;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProblemSubmission> submissions;
    
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestCase> testCases;
    
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    
    public enum Category {
        ARRAYS, STRINGS, LINKED_LIST, STACK, QUEUE, TREE, GRAPH, 
        DYNAMIC_PROGRAMMING, GREEDY, BACKTRACKING, SORTING, SEARCHING
    }
}
