package com.example.application.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.entity.DailyNote;

public interface DailyNoteRepository extends JpaRepository<DailyNote, Long> {
    List<DailyNote> findByDate(LocalDate date);
    List<DailyNote> findByDateAndUserId(LocalDate date, Long userId);
    
    void deleteByDateAndUserId(DailyNote note, Long userId);

    List<DailyNote> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<DailyNote> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

   
}