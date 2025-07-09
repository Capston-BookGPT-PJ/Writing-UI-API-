package com.example.project.controller;

import com.example.project.service.ReadingStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading-stats")
public class ReadingStatsController {
    private final ReadingStatsService readingStatsService;

    public ReadingStatsController(ReadingStatsService readingStatsService) {
        this.readingStatsService = readingStatsService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getReadingStats(@PathVariable Long userId) {
        return ResponseEntity.ok(readingStatsService.getReadingStats(userId));
    }
}