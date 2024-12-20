package com.example.application.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.entity.DailyMoodEntity;

import java.time.LocalDate;
import java.util.List;

public interface DailyMoodRepository extends JpaRepository<DailyMoodEntity, Long> {
    DailyMoodEntity findByDate(LocalDate date); // Find mood by date
    List<DailyMoodEntity> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, Long userId); // Find moods between dates
    
    
    
    DailyMoodEntity findByDateAndUserId(LocalDate date, Long userId);
    List<DailyMoodEntity> findByUserId(Long userId);
}
