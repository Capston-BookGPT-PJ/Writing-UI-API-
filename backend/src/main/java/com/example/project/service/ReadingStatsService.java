package com.example.project.service;

import org.springframework.stereotype.Service;

@Service
public class ReadingStatsService {
    public String getReadingStats(Long userId) {
        return "독서 통계 데이터 (예제)";
    }
}