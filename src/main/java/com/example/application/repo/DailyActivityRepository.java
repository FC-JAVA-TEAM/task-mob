package com.example.application.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.entity.DailyActivity;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Long> {
   
	
	 List<DailyActivity> findByDateAndUserId(LocalDate date, Long userId);
	List<DailyActivity> findByUserId(Long userId);
	List<DailyActivity> findByDate(LocalDate date);
	
    List<DailyActivity> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate,Long userId);
    
    List<DailyActivity> findByDateBetweenAndDescription(LocalDate startDate, LocalDate endDate,String userId);

    
    
    boolean existsByDateAndDescription(LocalDate date, String description);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM DailyActivity d WHERE d.description = ?1 AND d.date = ?2")
    void deleteByDescriptionAndDate(String description, LocalDate date);
}
