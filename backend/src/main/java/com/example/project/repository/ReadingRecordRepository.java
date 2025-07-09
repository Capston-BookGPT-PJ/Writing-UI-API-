package com.example.project.repository;

import com.example.project.entity.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    Optional<ReadingRecord> findByUserIdAndBookId(Long userId, Long bookId);
}