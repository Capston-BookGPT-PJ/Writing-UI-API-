package com.example.project.dto;

import com.example.project.entity.ReadingRecord;
import com.example.project.entity.User;
import com.example.project.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReadingRecordDto {
    private Long id;          // 기록 ID
    private Long userId;      // 유저 ID
    private Long bookId;      // 책 ID
    private int readPages;    // 읽은 페이지 수
    private int totalPages;   // 전체 페이지 수
    private double progress;  // 진행률 (%)
    private LocalDate startDate; // 읽기 시작한 날짜
    private LocalDate endDate;   // 다 읽은 날짜 (100% 시)

    // ✅ DTO -> 엔티티 변환 메서드
    public ReadingRecord convertToEntity(ReadingRecordDto dto, User user, Book book) {
        if (dto == null || user == null || book == null) {
            throw new IllegalArgumentException("Invalid input: DTO, User, or Book is null");
        }

        // 진행률 계산 (책의 전체 페이지 수를 활용)
        double progress = (book.getTotalPages() > 0) ? ((double) dto.getReadPages() / book.getTotalPages()) * 100 : 0;

        return new ReadingRecord(
            dto.getId(),  
            user,  
            book,  
            dto.getReadPages(),  
            progress,  // 책의 전체 페이지 수를 사용한 진행률 계산
            dto.getStartDate(),  
            dto.getEndDate()  
        );
    }

    // ✅ 엔티티 -> DTO 변환 메서드
    public static ReadingRecordDto fromEntity(ReadingRecord record) {
        return new ReadingRecordDto(
            record.getId(),
            record.getUser().getId(),
            record.getBook().getId(),
            record.getReadPages(),
            record.getBook().getTotalPages(),  // 수정된 부분: Book에서 totalPages 가져오기
            record.getProgress(),
            record.getStartDate(),
            record.getEndDate()
        );
    }
}