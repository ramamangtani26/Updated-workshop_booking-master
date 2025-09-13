package com.example.dsaassistant.repository;

import com.example.dsaassistant.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    
    List<Problem> findByDifficulty(Problem.Difficulty difficulty);
    
    List<Problem> findByCategory(Problem.Category category);
    
    List<Problem> findByIsActiveTrue();
    
    @Query("SELECT p FROM Problem p WHERE p.title LIKE %:title% OR p.description LIKE %:description%")
    List<Problem> findByTitleOrDescriptionContaining(@Param("title") String title, 
                                                   @Param("description") String description);
    
    @Query("SELECT p FROM Problem p WHERE p.difficulty = :difficulty AND p.isActive = true")
    List<Problem> findActiveProblemsByDifficulty(@Param("difficulty") Problem.Difficulty difficulty);
    
    @Query("SELECT p FROM Problem p WHERE p.category = :category AND p.isActive = true")
    List<Problem> findActiveProblemsByCategory(@Param("category") Problem.Category category);
    
    @Query("SELECT p FROM Problem p WHERE p.difficulty = :difficulty AND p.category = :category AND p.isActive = true")
    List<Problem> findActiveProblemsByDifficultyAndCategory(@Param("difficulty") Problem.Difficulty difficulty, 
                                                           @Param("category") Problem.Category category);
    
    @Query("SELECT p FROM Problem p WHERE :tag MEMBER OF p.tags AND p.isActive = true")
    List<Problem> findActiveProblemsByTag(@Param("tag") String tag);
    
    @Query("SELECT COUNT(p) FROM Problem p WHERE p.difficulty = :difficulty AND p.isActive = true")
    Long countActiveProblemsByDifficulty(@Param("difficulty") Problem.Difficulty difficulty);
    
    @Query("SELECT COUNT(p) FROM Problem p WHERE p.category = :category AND p.isActive = true")
    Long countActiveProblemsByCategory(@Param("category") Problem.Category category);
}
