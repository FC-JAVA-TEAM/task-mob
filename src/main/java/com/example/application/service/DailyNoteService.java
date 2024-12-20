// src/main/java/com/example/application/service/DailyNoteService.java
package com.example.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.application.entity.DailyNote;
import com.example.application.entity.MobUser;
import com.example.application.repo.DailyNoteRepository;

@Service
public class DailyNoteService {
    @Autowired
    private DailyNoteRepository dailyNoteRepository;
    
    @Autowired
	private AuthUserService authUserService;

    public DailyNote save(DailyNote note) {
        return dailyNoteRepository.save(note);
    }

    public void delete(DailyNote note) {
        dailyNoteRepository.delete(note);
    }
    
    public void deleteByDateIdAndUseId(DailyNote note,Long id) {
        dailyNoteRepository.deleteByDateAndUserId(note,id);
    }

    public List<DailyNote> findByDate(LocalDate date) {
        return dailyNoteRepository.findByDate(date);
    }
    
    public List<DailyNote> findByDateAndUserId(LocalDate date,Long id) {
        return dailyNoteRepository.findByDateAndUserId(date,id);
    }
    
    

    public List<DailyNote> findByMonth(LocalDate month) {
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
        return dailyNoteRepository.findByDateBetween(startDate, endDate);
    }

    
    public List<DailyNote> findByMonthAndUserId(LocalDate month,Long id) {
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
        return dailyNoteRepository.findByUserIdAndDateBetween(id, startDate,endDate);
    }
    public void update(DailyNote note) {
        dailyNoteRepository.save(note);
    }

    public List<DailyNote> findAll() {
        return dailyNoteRepository.findAll();
    }

	
	public MobUserDetails getAuthenticatedUsername() {
		MobUserDetails authenticatedUsername = authUserService.getAuthenticatedUsername();
		return authenticatedUsername;

	}

	public Optional<MobUser> checkUser(Long userId) {

		return authUserService.checkUser(userId);
	}
}