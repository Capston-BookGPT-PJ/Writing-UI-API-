package com.example.project.controller;

import com.example.project.dto.BookDto;
import com.example.project.dto.ReadingRecordDto; // ReadingRecordDto 임포트
import com.example.project.service.AladinService;
import com.example.project.entity.ReadingRecord;
import com.example.project.service.ReadingRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading-records")
public class ReadingRecordController {

    private final ReadingRecordService readingRecordService;
    private final AladinService aladinService;

    // 생성자 주입
    public ReadingRecordController(ReadingRecordService readingRecordService, AladinService aladinService) {
        this.readingRecordService = readingRecordService;
        this.aladinService = aladinService;
    }

    // 책 검색 API 추가
    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String keyword) {
        // 알라딘 API를 통해 책 검색
        List<BookDto> books = aladinService.searchBooks(keyword);
        return ResponseEntity.ok(books); // 검색된 책 리스트 반환
    }

    // 독서 기록 생성 API
    @PostMapping
    public ResponseEntity<ReadingRecord> createRecord(@RequestBody ReadingRecordDto dto) {
        // ReadingRecordDto에서 필요한 값 추출
        Long userId = dto.getUserId();     // 사용자 ID
        Long bookId = dto.getBookId();     // 책 ID
        int readPages = dto.getReadPages(); // 읽은 페이지 수

        // ReadingRecordService의 addReadingRecord 메서드 호출하여 독서 기록 생성
        ReadingRecord readingRecord = readingRecordService.addReadingRecord(userId, bookId, readPages);

        return ResponseEntity.ok(readingRecord); // 생성된 독서 기록 반환
    }
}