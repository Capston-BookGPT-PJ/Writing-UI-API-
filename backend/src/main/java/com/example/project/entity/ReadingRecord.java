package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reading_records")
public class ReadingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private int readPages;  // 읽은 페이지 수
    private double progress; // 진행률 (%)
    private LocalDate startDate;
    private LocalDate endDate;

    public void updateProgress() {
        if (book == null || book.getTotalPages() <= 0) {
            this.progress = 0;
            return;
        }

        // 진행률을 소수점 첫째 자리까지 반올림하여 저장
        this.progress = Math.round(((double) this.readPages / book.getTotalPages()) * 1000) / 10.0;

        // 진행률이 100%가 되었고, 아직 endDate가 설정되지 않았다면 현재 날짜로 설정
        if (this.progress >= 100 && this.endDate == null) {
            this.endDate = LocalDate.now();
        }
    }
}