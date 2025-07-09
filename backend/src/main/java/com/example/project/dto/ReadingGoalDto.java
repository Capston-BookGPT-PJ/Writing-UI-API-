package com.example.project.dto;

import com.example.project.entity.ReadingGoal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingGoalDto {
    private Long id;
    private Long userId;
    
    private int targetBooks;
    private int completedBooks;
    
    private int targetReviews;
    private int completedReviews;

    private int targetMinutes;
    private int completedMinutes;

    private LocalDate startDate;
    private LocalDate endDate;

    // 엔티티 → DTO 변환
    public static ReadingGoalDto fromEntity(ReadingGoal goal) {
        return new ReadingGoalDto(
            goal.getId(),
            goal.getUser().getId(),
            goal.getTargetBooks(),
            goal.getCompletedBooks(),
            goal.getTargetReviews(),
            goal.getCompletedReviews(),
            goal.getTargetMinutes(),
            goal.getCompletedMinutes(),
            goal.getStartDate(),
            goal.getEndDate()
        );
    }

    // DTO → 엔티티 변환
    public ReadingGoal toEntity() {
        ReadingGoal goal = new ReadingGoal();
        goal.setTargetBooks(this.targetBooks);
        goal.setCompletedBooks(this.completedBooks);
        goal.setTargetReviews(this.targetReviews);
        goal.setCompletedReviews(this.completedReviews);
        goal.setTargetMinutes(this.targetMinutes);
        goal.setCompletedMinutes(this.completedMinutes);
        goal.setStartDate(this.startDate);
        goal.setEndDate(this.endDate);
        return goal;
    }
}