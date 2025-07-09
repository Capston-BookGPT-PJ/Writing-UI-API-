package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reading_goals")
public class ReadingGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private GoalType goalType; // MONTHLY 또는 YEARLY

    private int targetBooks;  // 목표 도서 수
    private int completedBooks; // 완료한 도서 수

    private int targetReviews;  // 목표 감상문 개수
    private int completedReviews; // 완료한 감상문 개수

    private int targetMinutes;  // 목표 독서 시간 (분)
    private int completedMinutes; // 완료한 독서 시간 (분)

    private double bookProgress;  // 책 목표 진행률 (%)
    private double reviewProgress;  // 감상문 목표 진행률 (%)
    private double timeProgress;  // 독서 시간 목표 진행률 (%)

    private LocalDate startDate;
    private LocalDate endDate;

    // ✅ 진행률 업데이트 메서드
    public void updateProgress() {
        bookProgress = (targetBooks > 0) ? ((double) completedBooks / targetBooks) * 100 : 0;
        reviewProgress = (targetReviews > 0) ? ((double) completedReviews / targetReviews) * 100 : 0;
        timeProgress = (targetMinutes > 0) ? ((double) completedMinutes / targetMinutes) * 100 : 0;

        // 모든 목표를 달성하면 종료 날짜 기록
        if (bookProgress >= 100 && reviewProgress >= 100 && timeProgress >= 100) {
            this.endDate = LocalDate.now();
        }
    }

    // ✅ 목표 달성 업데이트 메서드 (책, 감상문, 독서 시간)
    public void completeBook() {
        this.completedBooks++;
        updateProgress();
    }

    public void completeReview() {
        this.completedReviews++;
        updateProgress();
    }

    public void addReadingTime(int minutes) {
        this.completedMinutes += minutes;
        updateProgress();
    }

    public enum GoalType {
        MONTHLY, YEARLY
    }
}