package com.example.project.controller;

import com.example.project.entity.ReadingGoal;
import com.example.project.service.ReadingGoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading-goals")
public class ReadingGoalController {
    private final ReadingGoalService readingGoalService;

    public ReadingGoalController(ReadingGoalService readingGoalService) {
        this.readingGoalService = readingGoalService;
    }

    @PostMapping
    public ResponseEntity<ReadingGoal> createGoal(@RequestBody ReadingGoal goal) {
        return ResponseEntity.ok(readingGoalService.saveGoal(goal));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReadingGoal>> getUserGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(readingGoalService.getUserGoals(userId));
    }

    @PostMapping("/update-progress/{userId}")
    public ResponseEntity<Void> updateGoalProgress(@PathVariable Long userId, 
                                                   @RequestParam int completedBooks,
                                                   @RequestParam int completedReviews,
                                                   @RequestParam int completedMinutes) {
        readingGoalService.updateGoalProgress(userId, completedBooks, completedReviews, completedMinutes);
        return ResponseEntity.ok().build();
    }
}