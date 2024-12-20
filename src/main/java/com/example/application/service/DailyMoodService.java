package com.example.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.application.dto.DailyMoodRequest;
import com.example.application.entity.DailyMoodEntity;
import com.example.application.entity.MobUser;
import com.example.application.exception.BadRequestException;
import com.example.application.exception.ResourceNotFoundException;
import com.example.application.repo.DailyMoodRepository;

@Service
public class DailyMoodService {

	@Autowired
	private DailyMoodRepository dailyMoodRepository;

	@Autowired
	AuthUserService authUserService;

	// Save or update the DailyMoodEntity
	public DailyMoodEntity save(DailyMoodEntity mood) {
		return dailyMoodRepository.save(mood);
	}

	// Delete the DailyMoodEntity
	public void delete(DailyMoodEntity mood) {
		dailyMoodRepository.delete(mood);
	}

	// Find a DailyMoodEntity by date
	public DailyMoodEntity findByDate(LocalDate date) {
		return dailyMoodRepository.findByDate(date);
	}

	// Update a DailyMoodEntity
	public void update(DailyMoodEntity mood) {
		dailyMoodRepository.save(mood);
	}

	// Find DailyMoodEntities by date range
	public List<DailyMoodEntity> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate) {

		List<DailyMoodEntity> byDateBetweenAndUserId = dailyMoodRepository.findByDateBetweenAndUserId(startDate,
				endDate, authUserService.getAuthenticatedUsername().getUserId());
		return byDateBetweenAndUserId;
	}

	// Get all DailyMoodEntities, throws exception if not found
	public List<DailyMoodEntity> getAllDailyMoods() {
		List<DailyMoodEntity> all = dailyMoodRepository.findAll();
		if (all.isEmpty()) {
			throw new ResourceNotFoundException("No daily mood records found.");
		}
		return all;
	}

	// Delete DailyMoodEntity by ID
	public boolean deleteDailyMood(Long id) {
		Optional<DailyMoodEntity> existingMood = dailyMoodRepository.findById(id);
		if (existingMood.isPresent()) {
			dailyMoodRepository.delete(existingMood.get());
			return true;
		} else {
			return false; // Return false if not found
		}
	}

	// Get moods by user ID
	public List<DailyMoodEntity> getMoodsByUserId(Long userId) {
		List<DailyMoodEntity> moods = dailyMoodRepository.findByUserId(userId);
		if (moods.isEmpty()) {
			throw new ResourceNotFoundException("No moods found for userId: " + userId);
		}
		return moods;
	}

	// Update a DailyMoodEntity by ID
	public DailyMoodEntity updateDailyMood(Long id, DailyMoodRequest dailyMoodRequest) {
		Optional<DailyMoodEntity> existingMood = dailyMoodRepository.findById(id);
		if (existingMood.isPresent()) {
			DailyMoodEntity moodEntity = existingMood.get();
			validateAndUpdateMood(moodEntity, dailyMoodRequest);
			return dailyMoodRepository.save(moodEntity);
		} else {
			throw new ResourceNotFoundException("Mood not found for the given ID");
		}
	}

	// Create a new DailyMoodEntity
	public DailyMoodEntity createDailyMood(DailyMoodRequest dailyMoodRequest) {

		// Validate the request fields
		if (dailyMoodRequest == null || dailyMoodRequest.getDate() == null || dailyMoodRequest.getMood() == null) {
			throw new BadRequestException("Missing required fields: date or mood.");
		}

		// Find the user by ID
		Long userId = dailyMoodRequest.getUserId();
		Optional<MobUser> userOptional = checkUser(userId);

		// Check if a mood already exists for this user and date
		DailyMoodEntity existingMood = dailyMoodRepository.findByDateAndUserId(dailyMoodRequest.getDate(), userId);
		if (existingMood != null) {
			throw new BadRequestException("Mood already exists for the given date and user.");
		}

		// Create new DailyMoodEntity
		DailyMoodEntity dailyMoodEntity = new DailyMoodEntity();
		dailyMoodEntity.setDate(dailyMoodRequest.getDate());
		dailyMoodEntity.setMood(dailyMoodRequest.getMood());
		dailyMoodEntity.setUser(userOptional.get());

		// Save the new entity
		return dailyMoodRepository.save(dailyMoodEntity);
	}

	// Get DailyMoodEntity by date and user ID
	public DailyMoodEntity getMoodByDateAndUserId(LocalDate date, Long userId) {
		checkUser(userId); // Ensure the user exists
		DailyMoodEntity dailyMoodEntity = dailyMoodRepository.findByDateAndUserId(date, userId);
		if (dailyMoodEntity == null) {
			// throw new ResourceNotFoundException("No mood found for the given date and
			// userId");
		}
		return dailyMoodEntity;
	}

	// Helper method to validate and update the mood
	private void validateAndUpdateMood(DailyMoodEntity moodEntity, DailyMoodRequest dailyMoodRequest) {
		if (dailyMoodRequest.getDate() != null) {
			moodEntity.setDate(dailyMoodRequest.getDate());
		}
		if (dailyMoodRequest.getMood() != null) {
			moodEntity.setMood(dailyMoodRequest.getMood());
		}
	}

	 public MobUserDetails getAuthenticatedUsername() {
		 MobUserDetails authenticatedUsername = authUserService.getAuthenticatedUsername();
		 return authenticatedUsername;

	    }
	
	public Optional<MobUser> checkUser(Long userId) {

		return authUserService.checkUser(userId);
	}

}
