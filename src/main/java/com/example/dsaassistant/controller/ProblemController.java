package com.example.dsaassistant.controller;

import com.example.dsaassistant.model.Problem;
import com.example.dsaassistant.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProblemController {
    
    private final ProblemService problemService;
    
    @PostMapping
    public ResponseEntity<Problem> createProblem(@Valid @RequestBody Problem problem) {
        try {
            Problem createdProblem = problemService.createProblem(problem);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProblem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Problem>> getAllProblems() {
        List<Problem> problems = problemService.getAllProblems();
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Problem> getProblemById(@PathVariable Long id) {
        return problemService.getProblemById(id)
                .map(problem -> ResponseEntity.ok(problem))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Problem>> getProblemsByDifficulty(@PathVariable Problem.Difficulty difficulty) {
        List<Problem> problems = problemService.getProblemsByDifficulty(difficulty);
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Problem>> getProblemsByCategory(@PathVariable Problem.Category category) {
        List<Problem> problems = problemService.getProblemsByCategory(category);
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Problem>> getActiveProblems() {
        List<Problem> problems = problemService.getActiveProblems();
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/active/difficulty/{difficulty}")
    public ResponseEntity<List<Problem>> getActiveProblemsByDifficulty(@PathVariable Problem.Difficulty difficulty) {
        List<Problem> problems = problemService.getActiveProblemsByDifficulty(difficulty);
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/active/category/{category}")
    public ResponseEntity<List<Problem>> getActiveProblemsByCategory(@PathVariable Problem.Category category) {
        List<Problem> problems = problemService.getActiveProblemsByCategory(category);
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/active/difficulty/{difficulty}/category/{category}")
    public ResponseEntity<List<Problem>> getActiveProblemsByDifficultyAndCategory(
            @PathVariable Problem.Difficulty difficulty, 
            @PathVariable Problem.Category category) {
        List<Problem> problems = problemService.getActiveProblemsByDifficultyAndCategory(difficulty, category);
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/active/tag/{tag}")
    public ResponseEntity<List<Problem>> getActiveProblemsByTag(@PathVariable String tag) {
        List<Problem> problems = problemService.getActiveProblemsByTag(tag);
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Problem>> searchProblems(@RequestParam String q) {
        List<Problem> problems = problemService.searchProblems(q);
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/stats/difficulty/{difficulty}")
    public ResponseEntity<Long> getProblemCountByDifficulty(@PathVariable Problem.Difficulty difficulty) {
        Long count = problemService.countActiveProblemsByDifficulty(difficulty);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/category/{category}")
    public ResponseEntity<Long> getProblemCountByCategory(@PathVariable Problem.Category category) {
        Long count = problemService.countActiveProblemsByCategory(category);
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Problem> updateProblem(@PathVariable Long id, @Valid @RequestBody Problem problemDetails) {
        try {
            Problem updatedProblem = problemService.updateProblem(id, problemDetails);
            return ResponseEntity.ok(updatedProblem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Problem> deactivateProblem(@PathVariable Long id) {
        try {
            Problem problem = problemService.deactivateProblem(id);
            return ResponseEntity.ok(problem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        try {
            problemService.deleteProblem(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
