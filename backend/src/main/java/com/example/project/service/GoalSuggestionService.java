package com.example.project.service;

import org.springframework.stereotype.Service;

@Service
public class GoalSuggestionService {
    public String suggestGoal(Long userId) {
        return "지난달에는 3권을 읽었습니다. 이번 달에는 4권 읽기에 도전해보는 건 어떨까요?";
    }
}