package com.example.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.entity.DailyActivity;
import com.example.application.entity.MobUser;
import com.example.application.exception.ResourceNotFoundException;
import com.example.application.repo.DailyActivityRepository;


@Service
@Transactional
public class DailyActivityService {
    @Autowired
    private DailyActivityRepository dailyActivityRepository;

    
    
    @Autowired
    AuthUserService authUserService;

    
    public void deleteActivity(String description, LocalDate date) {
        dailyActivityRepository.deleteByDescriptionAndDate(description, date);
    }
    
   
    public DailyActivity save(DailyActivity activity) {
        return dailyActivityRepository.save(activity);
    }
    
    public List<DailyActivity> saveAllActivity(List<DailyActivity> activity) {
        return dailyActivityRepository.saveAll(activity);
    }

    public void delete(DailyActivity activity) {
        dailyActivityRepository.delete(activity);
    }

    public List<DailyActivity> findByUserId(Long id) {
    	return dailyActivityRepository.findByUserId(id);
    }
    	public List<DailyActivity> findAll() {
        return dailyActivityRepository.findAll();
    }

    public List<DailyActivity> findByDate(LocalDate date) {
        return dailyActivityRepository.findByDate(date);
    }
    
    public List<DailyActivity> findByDateAndUserId(LocalDate date,Long id) {
        return dailyActivityRepository.findByDateAndUserId(date,id);
    }


    public boolean existsByDateAndDescription(LocalDate date, String description) {
        return dailyActivityRepository.existsByDateAndDescription(date, description);
    }

    public void update(DailyActivity activity) {
        dailyActivityRepository.save(activity);
    }
    
    public List<DailyActivity> findByDateBetween(LocalDate startDate, LocalDate endDate , Long id) {
         List<DailyActivity> byDateBetweenAndUserId = dailyActivityRepository.findByDateBetweenAndUserId(startDate, endDate,id);
		return byDateBetweenAndUserId;
  
    }
    
    public List<DailyActivity> findByDateBetweenWithDescription(LocalDate startDate, LocalDate endDate ,String description ) {
          List<DailyActivity> byDateBetweenAndUserId = dailyActivityRepository.findByDateBetweenAndDescription(startDate, endDate,description);
 		return byDateBetweenAndUserId;
   
     }


    public void deleteActivitiesInRange(String description, LocalDate startDate, LocalDate endDate) {
        // Fetch activities in the given date range
    	
    	Long userId = authUserService.getAuthenticatedUsername().getUserId();
       // List<DailyActivity> activities = dailyActivityRepository.findByDateBetweenAndUserId(startDate, endDate,getAuthenticatedUsername().getUserId());
        List<DailyActivity> activities = dailyActivityRepository.findByDateBetweenAndUserId(startDate, endDate,userId);
        
        // Iterate over the fetched activities
        for (DailyActivity activity : activities) {
            // Check if the activity description matches and delete it
            if (activity.getDescription().equalsIgnoreCase(description)) {
            	dailyActivityRepository.delete(activity);
            }
        }
    }
    
    
  
    public MobUserDetails getAuthenticatedUsername() {
       return authUserService.getAuthenticatedUsername();
    }
    
    public Optional<MobUser> checkUser(Long userId) {
      
        return authUserService.checkUser(userId);
    }
    
}