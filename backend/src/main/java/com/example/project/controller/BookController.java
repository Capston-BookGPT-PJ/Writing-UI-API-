package com.example.project.controller;

import com.example.project.service.AladinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final AladinService aladinService;

    // 📌 책 검색 API - 사용자가 입력한 키워드로 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam String keyword) {
        return ResponseEntity.ok(aladinService.searchBooks(keyword));
    }
}