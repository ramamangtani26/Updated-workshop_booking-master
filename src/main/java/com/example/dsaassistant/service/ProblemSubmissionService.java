package com.example.dsaassistant.service;

import com.example.dsaassistant.model.Problem;
import com.example.dsaassistant.model.ProblemSubmission;
import com.example.dsaassistant.model.User;
import com.example.dsaassistant.repository.ProblemSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemSubmissionService {
    
    private final ProblemSubmissionRepository submissionRepository;
    
    public ProblemSubmission createSubmission(ProblemSubmission submission) {
        return submissionRepository.save(submission);
    }
    
    public ProblemSubmission updateSubmission(Long id, ProblemSubmission submissionDetails) {
        ProblemSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with id: " + id));
        
        submission.setStatus(submissionDetails.getStatus());
        submission.setExecutionTimeMs(submissionDetails.getExecutionTimeMs());
        submission.setMemoryUsedMB(submissionDetails.getMemoryUsedMB());
        submission.setTestCasesPassed(submissionDetails.getTestCasesPassed());
        submission.setTotalTestCases(submissionDetails.getTotalTestCases());
        submission.setErrorMessage(submissionDetails.getErrorMessage());
        
        return submissionRepository.save(submission);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getAllSubmissions() {
        return submissionRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<ProblemSubmission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getSubmissionsByUser(User user) {
        return submissionRepository.findByUser(user);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getSubmissionsByProblem(Problem problem) {
        return submissionRepository.findByProblem(problem);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getSubmissionsByStatus(ProblemSubmission.SubmissionStatus status) {
        return submissionRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getSubmissionsByLanguage(ProblemSubmission.Language language) {
        return submissionRepository.findByLanguage(language);
    }
    
    @Transactional(readOnly = true)
    public Optional<ProblemSubmission> getSubmissionByUserAndProblem(User user, Problem problem) {
        return submissionRepository.findByUserAndProblem(user, problem);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getSubmissionsByUserAndStatus(User user, ProblemSubmission.SubmissionStatus status) {
        return submissionRepository.findByUserAndStatus(user, status);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getAcceptedSubmissionsByProblem(Problem problem) {
        return submissionRepository.findAcceptedSubmissionsByProblem(problem);
    }
    
    @Transactional(readOnly = true)
    public Optional<ProblemSubmission> getAcceptedSubmissionByUserAndProblem(User user, Problem problem) {
        return submissionRepository.findAcceptedSubmissionByUserAndProblem(user, problem);
    }
    
    @Transactional(readOnly = true)
    public Long countAcceptedSubmissionsByUser(User user) {
        return submissionRepository.countAcceptedSubmissionsByUser(user);
    }
    
    @Transactional(readOnly = true)
    public Long countAcceptedSubmissionsByProblem(Problem problem) {
        return submissionRepository.countAcceptedSubmissionsByProblem(problem);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getSubmissionsByUserAndDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return submissionRepository.findByUserAndSubmittedAtBetween(user, startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getSubmissionsByUserAndProblemDifficulty(User user, Problem.Difficulty difficulty) {
        return submissionRepository.findByUserAndProblemDifficulty(user, difficulty);
    }
    
    @Transactional(readOnly = true)
    public List<ProblemSubmission> getSubmissionsByUserAndProblemCategory(User user, Problem.Category category) {
        return submissionRepository.findByUserAndProblemCategory(user, category);
    }
    
    @Transactional(readOnly = true)
    public boolean hasUserSubmittedProblem(User user, Problem problem) {
        return submissionRepository.existsByUserAndProblem(user, problem);
    }
    
    public void deleteSubmission(Long id) {
        ProblemSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with id: " + id));
        submissionRepository.delete(submission);
    }
}
