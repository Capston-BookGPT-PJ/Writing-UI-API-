package com.example.project.repository;

import com.example.project.entity.ReadingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReadingGoalRepository extends JpaRepository<ReadingGoal, Long> {
    List<ReadingGoal> findByUserId(Long userId);
}