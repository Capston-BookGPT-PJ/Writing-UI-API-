package com.example.project.service;

import com.example.project.entity.ReadingGoal;
import com.example.project.repository.ReadingGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReadingGoalService {
    private final ReadingGoalRepository readingGoalRepository;

    public ReadingGoalService(ReadingGoalRepository readingGoalRepository) {
        this.readingGoalRepository = readingGoalRepository;
    }

    public ReadingGoal saveGoal(ReadingGoal goal) {
        return readingGoalRepository.save(goal);
    }

    public List<ReadingGoal> getUserGoals(Long userId) {
        return readingGoalRepository.findByUserId(userId);
    }

    @Transactional
    public void updateGoalProgress(Long userId, int completedBooks, int completedReviews, int completedMinutes) {
        List<ReadingGoal> goals = readingGoalRepository.findByUserId(userId);
        for (ReadingGoal goal : goals) {
            goal.setCompletedBooks(goal.getCompletedBooks() + completedBooks);
            goal.setCompletedReviews(goal.getCompletedReviews() + completedReviews);
            goal.setCompletedMinutes(goal.getCompletedMinutes() + completedMinutes);
            goal.updateProgress();
            readingGoalRepository.save(goal);
        }
    }
}