package com.example.dsaassistant.repository;

import com.example.dsaassistant.model.Problem;
import com.example.dsaassistant.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    
    List<TestCase> findByProblem(Problem problem);
    
    List<TestCase> findByProblemAndIsSample(Problem problem, Boolean isSample);
    
    List<TestCase> findByProblemAndIsHidden(Problem problem, Boolean isHidden);
    
    @Query("SELECT t FROM TestCase t WHERE t.problem = :problem AND t.isSample = true")
    List<TestCase> findSampleTestCasesByProblem(@Param("problem") Problem problem);
    
    @Query("SELECT t FROM TestCase t WHERE t.problem = :problem AND t.isHidden = false")
    List<TestCase> findVisibleTestCasesByProblem(@Param("problem") Problem problem);
    
    @Query("SELECT COUNT(t) FROM TestCase t WHERE t.problem = :problem")
    Long countTestCasesByProblem(@Param("problem") Problem problem);
    
    @Query("SELECT COUNT(t) FROM TestCase t WHERE t.problem = :problem AND t.isSample = true")
    Long countSampleTestCasesByProblem(@Param("problem") Problem problem);
}
