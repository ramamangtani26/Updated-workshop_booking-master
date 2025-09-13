package com.example.dsaassistant.service;

import com.example.dsaassistant.model.Problem;
import com.example.dsaassistant.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemService {
    
    private final ProblemRepository problemRepository;
    
    public Problem createProblem(Problem problem) {
        return problemRepository.save(problem);
    }
    
    public Problem updateProblem(Long id, Problem problemDetails) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        
        problem.setTitle(problemDetails.getTitle());
        problem.setDescription(problemDetails.getDescription());
        problem.setProblemStatement(problemDetails.getProblemStatement());
        problem.setConstraints(problemDetails.getConstraints());
        problem.setExamples(problemDetails.getExamples());
        problem.setExpectedOutput(problemDetails.getExpectedOutput());
        problem.setDifficulty(problemDetails.getDifficulty());
        problem.setCategory(problemDetails.getCategory());
        problem.setTags(problemDetails.getTags());
        problem.setTimeLimitSeconds(problemDetails.getTimeLimitSeconds());
        problem.setMemoryLimitMB(problemDetails.getMemoryLimitMB());
        problem.setIsActive(problemDetails.getIsActive());
        
        return problemRepository.save(problem);
    }
    
    @Transactional(readOnly = true)
    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Problem> getProblemById(Long id) {
        return problemRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Problem> getProblemsByDifficulty(Problem.Difficulty difficulty) {
        return problemRepository.findByDifficulty(difficulty);
    }
    
    @Transactional(readOnly = true)
    public List<Problem> getProblemsByCategory(Problem.Category category) {
        return problemRepository.findByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public List<Problem> getActiveProblems() {
        return problemRepository.findByIsActiveTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Problem> searchProblems(String searchTerm) {
        return problemRepository.findByTitleOrDescriptionContaining(searchTerm, searchTerm);
    }
    
    @Transactional(readOnly = true)
    public List<Problem> getActiveProblemsByDifficulty(Problem.Difficulty difficulty) {
        return problemRepository.findActiveProblemsByDifficulty(difficulty);
    }
    
    @Transactional(readOnly = true)
    public List<Problem> getActiveProblemsByCategory(Problem.Category category) {
        return problemRepository.findActiveProblemsByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public List<Problem> getActiveProblemsByDifficultyAndCategory(Problem.Difficulty difficulty, Problem.Category category) {
        return problemRepository.findActiveProblemsByDifficultyAndCategory(difficulty, category);
    }
    
    @Transactional(readOnly = true)
    public List<Problem> getActiveProblemsByTag(String tag) {
        return problemRepository.findActiveProblemsByTag(tag);
    }
    
    @Transactional(readOnly = true)
    public Long countActiveProblemsByDifficulty(Problem.Difficulty difficulty) {
        return problemRepository.countActiveProblemsByDifficulty(difficulty);
    }
    
    @Transactional(readOnly = true)
    public Long countActiveProblemsByCategory(Problem.Category category) {
        return problemRepository.countActiveProblemsByCategory(category);
    }
    
    public void deleteProblem(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        problemRepository.delete(problem);
    }
    
    public Problem deactivateProblem(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        problem.setIsActive(false);
        return problemRepository.save(problem);
    }
}
