package com.example.dsaassistant.controller;

import com.example.dsaassistant.model.Problem;
import com.example.dsaassistant.model.ProblemSubmission;
import com.example.dsaassistant.model.User;
import com.example.dsaassistant.service.ProblemSubmissionService;
import com.example.dsaassistant.service.ProblemService;
import com.example.dsaassistant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProblemSubmissionController {
    
    private final ProblemSubmissionService submissionService;
    private final UserService userService;
    private final ProblemService problemService;
    
    @PostMapping
    public ResponseEntity<ProblemSubmission> createSubmission(@RequestBody SubmissionRequest request) {
        try {
            User user = userService.getUserById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Problem problem = problemService.getProblemById(request.getProblemId())
                    .orElseThrow(() -> new RuntimeException("Problem not found"));
            
            ProblemSubmission submission = new ProblemSubmission();
            submission.setUser(user);
            submission.setProblem(problem);
            submission.setCode(request.getCode());
            submission.setLanguage(request.getLanguage());
            submission.setStatus(ProblemSubmission.SubmissionStatus.PENDING);
            
            ProblemSubmission createdSubmission = submissionService.createSubmission(submission);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubmission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ProblemSubmission>> getAllSubmissions() {
        List<ProblemSubmission> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProblemSubmission> getSubmissionById(@PathVariable Long id) {
        return submissionService.getSubmissionById(id)
                .map(submission -> ResponseEntity.ok(submission))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProblemSubmission>> getSubmissionsByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ProblemSubmission> submissions = submissionService.getSubmissionsByUser(user);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<List<ProblemSubmission>> getSubmissionsByProblem(@PathVariable Long problemId) {
        Problem problem = problemService.getProblemById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        List<ProblemSubmission> submissions = submissionService.getSubmissionsByProblem(problem);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProblemSubmission>> getSubmissionsByStatus(@PathVariable ProblemSubmission.SubmissionStatus status) {
        List<ProblemSubmission> submissions = submissionService.getSubmissionsByStatus(status);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/language/{language}")
    public ResponseEntity<List<ProblemSubmission>> getSubmissionsByLanguage(@PathVariable ProblemSubmission.Language language) {
        List<ProblemSubmission> submissions = submissionService.getSubmissionsByLanguage(language);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<ProblemSubmission>> getSubmissionsByUserAndStatus(
            @PathVariable Long userId, 
            @PathVariable ProblemSubmission.SubmissionStatus status) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ProblemSubmission> submissions = submissionService.getSubmissionsByUserAndStatus(user, status);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/user/{userId}/difficulty/{difficulty}")
    public ResponseEntity<List<ProblemSubmission>> getSubmissionsByUserAndDifficulty(
            @PathVariable Long userId, 
            @PathVariable Problem.Difficulty difficulty) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ProblemSubmission> submissions = submissionService.getSubmissionsByUserAndProblemDifficulty(user, difficulty);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<ProblemSubmission>> getSubmissionsByUserAndCategory(
            @PathVariable Long userId, 
            @PathVariable Problem.Category category) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ProblemSubmission> submissions = submissionService.getSubmissionsByUserAndProblemCategory(user, category);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/user/{userId}/accepted")
    public ResponseEntity<Long> getAcceptedSubmissionsCountByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long count = submissionService.countAcceptedSubmissionsByUser(user);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/problem/{problemId}/accepted")
    public ResponseEntity<Long> getAcceptedSubmissionsCountByProblem(@PathVariable Long problemId) {
        Problem problem = problemService.getProblemById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        Long count = submissionService.countAcceptedSubmissionsByProblem(problem);
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProblemSubmission> updateSubmission(@PathVariable Long id, @RequestBody ProblemSubmission submissionDetails) {
        try {
            ProblemSubmission updatedSubmission = submissionService.updateSubmission(id, submissionDetails);
            return ResponseEntity.ok(updatedSubmission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        try {
            submissionService.deleteSubmission(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Inner class for submission request
    public static class SubmissionRequest {
        private Long userId;
        private Long problemId;
        private String code;
        private ProblemSubmission.Language language;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getProblemId() { return problemId; }
        public void setProblemId(Long problemId) { this.problemId = problemId; }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public ProblemSubmission.Language getLanguage() { return language; }
        public void setLanguage(ProblemSubmission.Language language) { this.language = language; }
    }
}
