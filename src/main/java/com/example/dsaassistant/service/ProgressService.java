package com.example.dsaassistant.service;

import com.example.dsaassistant.model.Progress;
import com.example.dsaassistant.model.User;
import com.example.dsaassistant.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressService {
    
    private final ProgressRepository progressRepository;
    
    public Progress createProgress(Progress progress) {
        return progressRepository.save(progress);
    }
    
    public Progress updateProgress(Long id, Progress progressDetails) {
        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress not found with id: " + id));
        
        progress.setType(progressDetails.getType());
        progress.setCategory(progressDetails.getCategory());
        progress.setDifficulty(progressDetails.getDifficulty());
        progress.setProblemsSolved(progressDetails.getProblemsSolved());
        progress.setTotalProblems(progressDetails.getTotalProblems());
        progress.setAccuracyPercentage(progressDetails.getAccuracyPercentage());
        progress.setAverageTimeMs(progressDetails.getAverageTimeMs());
        
        return progressRepository.save(progress);
    }
    
    @Transactional(readOnly = true)
    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Progress> getProgressById(Long id) {
        return progressRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Progress> getProgressByUser(User user) {
        return progressRepository.findByUser(user);
    }
    
    @Transactional(readOnly = true)
    public List<Progress> getProgressByType(Progress.ProgressType type) {
        return progressRepository.findByType(type);
    }
    
    @Transactional(readOnly = true)
    public List<Progress> getProgressByUserAndType(User user, Progress.ProgressType type) {
        return progressRepository.findByUserAndType(user, type);
    }
    
    @Transactional(readOnly = true)
    public List<Progress> getProgressByUserAndCategory(User user, String category) {
        return progressRepository.findByUserAndCategory(user, category);
    }
    
    @Transactional(readOnly = true)
    public List<Progress> getProgressByUserAndDifficulty(User user, String difficulty) {
        return progressRepository.findByUserAndDifficulty(user, difficulty);
    }
    
    @Transactional(readOnly = true)
    public Optional<Progress> getProgressByUserAndTypeAndCategory(User user, Progress.ProgressType type, String category) {
        return progressRepository.findByUserAndTypeAndCategory(user, type, category);
    }
    
    @Transactional(readOnly = true)
    public Optional<Progress> getProgressByUserAndTypeAndDifficulty(User user, Progress.ProgressType type, String difficulty) {
        return progressRepository.findByUserAndTypeAndDifficulty(user, type, difficulty);
    }
    
    @Transactional(readOnly = true)
    public Optional<Progress> getOverallProgressByUser(User user) {
        return progressRepository.findOverallProgressByUser(user);
    }
    
    public void deleteProgress(Long id) {
        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress not found with id: " + id));
        progressRepository.delete(progress);
    }
}
