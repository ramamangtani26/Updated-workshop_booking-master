package com.example.dsaassistant.repository;

import com.example.dsaassistant.model.Problem;
import com.example.dsaassistant.model.ProblemSubmission;
import com.example.dsaassistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemSubmissionRepository extends JpaRepository<ProblemSubmission, Long> {
    
    List<ProblemSubmission> findByUser(User user);
    
    List<ProblemSubmission> findByProblem(Problem problem);
    
    List<ProblemSubmission> findByStatus(ProblemSubmission.SubmissionStatus status);
    
    List<ProblemSubmission> findByLanguage(ProblemSubmission.Language language);
    
    Optional<ProblemSubmission> findByUserAndProblem(User user, Problem problem);
    
    @Query("SELECT s FROM ProblemSubmission s WHERE s.user = :user AND s.status = :status")
    List<ProblemSubmission> findByUserAndStatus(@Param("user") User user, 
                                               @Param("status") ProblemSubmission.SubmissionStatus status);
    
    @Query("SELECT s FROM ProblemSubmission s WHERE s.problem = :problem AND s.status = 'ACCEPTED'")
    List<ProblemSubmission> findAcceptedSubmissionsByProblem(@Param("problem") Problem problem);
    
    @Query("SELECT s FROM ProblemSubmission s WHERE s.user = :user AND s.problem = :problem AND s.status = 'ACCEPTED'")
    Optional<ProblemSubmission> findAcceptedSubmissionByUserAndProblem(@Param("user") User user, 
                                                                      @Param("problem") Problem problem);
    
    @Query("SELECT COUNT(s) FROM ProblemSubmission s WHERE s.user = :user AND s.status = 'ACCEPTED'")
    Long countAcceptedSubmissionsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(s) FROM ProblemSubmission s WHERE s.problem = :problem AND s.status = 'ACCEPTED'")
    Long countAcceptedSubmissionsByProblem(@Param("problem") Problem problem);
    
    @Query("SELECT s FROM ProblemSubmission s WHERE s.user = :user AND s.submittedAt >= :startDate AND s.submittedAt <= :endDate")
    List<ProblemSubmission> findByUserAndSubmittedAtBetween(@Param("user") User user, 
                                                           @Param("startDate") LocalDateTime startDate, 
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM ProblemSubmission s WHERE s.problem.difficulty = :difficulty AND s.user = :user")
    List<ProblemSubmission> findByUserAndProblemDifficulty(@Param("user") User user, 
                                                          @Param("difficulty") Problem.Difficulty difficulty);
    
    @Query("SELECT s FROM ProblemSubmission s WHERE s.problem.category = :category AND s.user = :user")
    List<ProblemSubmission> findByUserAndProblemCategory(@Param("user") User user, 
                                                        @Param("category") Problem.Category category);
    
    boolean existsByUserAndProblem(User user, Problem problem);
}
