package com.example.project.service;

import com.example.project.entity.ReadingRecord;
import com.example.project.entity.ReadingGoal;
import com.example.project.entity.User;
import com.example.project.entity.Book;
import com.example.project.repository.ReadingRecordRepository;
import com.example.project.repository.ReadingGoalRepository;
import com.example.project.repository.UserRepository;
import com.example.project.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final ReadingGoalRepository readingGoalRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ReadingRecord addReadingRecord(Long userId, Long bookId, int readPages) {
        // 1. 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 책 존재 여부 확인
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("책을 찾을 수 없습니다."));

        // 3. 기존 독서 기록 조회
        Optional<ReadingRecord> recordOpt = readingRecordRepository.findByUserIdAndBookId(userId, bookId);
        ReadingRecord record;

        if (recordOpt.isPresent()) {
            // 기존 기록이 있는 경우 업데이트
            record = recordOpt.get();
            record.setReadPages(record.getReadPages() + readPages);
        } else {
            // 새 기록 생성
            record = new ReadingRecord();
            record.setUser(user);
            record.setBook(book);
            record.setReadPages(readPages);
            record.setStartDate(LocalDate.now());
        }

        // 4. 진행률 업데이트 (책의 전체 페이지 수 기반)
        int totalPages = book.getTotalPages(); // 책의 전체 페이지 수 가져오기
        if (totalPages > 0) {
            record.setProgress(((double) record.getReadPages() / totalPages) * 100);
        } else {
            record.setProgress(0); // 전체 페이지 수가 없는 경우 진행률 0으로 설정
        }

        // 5. 독서 목표 업데이트
        if (record.getProgress() >= 100) {
            List<ReadingGoal> goals = readingGoalRepository.findByUserId(userId);
            for (ReadingGoal goal : goals) {
                goal.setCompletedBooks(goal.getCompletedBooks() + 1); // 목표 달성 책 수 증가
                goal.updateProgress(); // 목표 진행률 업데이트
                readingGoalRepository.save(goal); // 목표 업데이트 저장
            }
        }

        // 6. 독서 기록 저장 후 반환
        return readingRecordRepository.save(record);
    }
}