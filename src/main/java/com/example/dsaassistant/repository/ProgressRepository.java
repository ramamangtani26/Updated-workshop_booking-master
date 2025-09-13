package com.example.dsaassistant.repository;

import com.example.dsaassistant.model.Progress;
import com.example.dsaassistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    
    List<Progress> findByUser(User user);
    
    List<Progress> findByType(Progress.ProgressType type);
    
    @Query("SELECT p FROM Progress p WHERE p.user = :user AND p.type = :type")
    List<Progress> findByUserAndType(@Param("user") User user, @Param("type") Progress.ProgressType type);
    
    @Query("SELECT p FROM Progress p WHERE p.user = :user AND p.category = :category")
    List<Progress> findByUserAndCategory(@Param("user") User user, @Param("category") String category);
    
    @Query("SELECT p FROM Progress p WHERE p.user = :user AND p.difficulty = :difficulty")
    List<Progress> findByUserAndDifficulty(@Param("user") User user, @Param("difficulty") String difficulty);
    
    @Query("SELECT p FROM Progress p WHERE p.user = :user AND p.type = :type AND p.category = :category")
    Optional<Progress> findByUserAndTypeAndCategory(@Param("user") User user, 
                                                   @Param("type") Progress.ProgressType type, 
                                                   @Param("category") String category);
    
    @Query("SELECT p FROM Progress p WHERE p.user = :user AND p.type = :type AND p.difficulty = :difficulty")
    Optional<Progress> findByUserAndTypeAndDifficulty(@Param("user") User user, 
                                                     @Param("type") Progress.ProgressType type, 
                                                     @Param("difficulty") String difficulty);
    
    @Query("SELECT p FROM Progress p WHERE p.user = :user AND p.type = 'OVERALL'")
    Optional<Progress> findOverallProgressByUser(@Param("user") User user);
}
