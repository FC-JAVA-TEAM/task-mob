package com.example.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.application.entity.MobUser;
import com.example.application.entity.NamajEntry;
import com.example.application.repo.NamajEntryRepository;

@Service
public class DailyNamajService {
	@Autowired
	private NamajEntryRepository namajEntryRepository;

	@Autowired
	private AuthUserService authUserService;

	public void save(NamajEntry entry) {
		namajEntryRepository.save(entry);
	}

	public void update(NamajEntry entry) {
		namajEntryRepository.save(entry);
	}

	public List<NamajEntry> findByDate(LocalDate date) {
		return namajEntryRepository.findByNamazDate(date);
	}

	public void delete(NamajEntry entry) {
		namajEntryRepository.delete(entry);
	}

	public Optional<NamajEntry> findByDateAndType(LocalDate date, String namazType) {
		return namajEntryRepository.findByNamazDateAndNamazType(date, namazType);
	}

	public List<NamajEntry> findByMonth(LocalDate month) {
		LocalDate startDate = month.withDayOfMonth(1);
		LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
		return namajEntryRepository.findByNamazDateBetween(startDate, endDate);
	}

	public List<NamajEntry> findByNamazDateBetweenAndUserId(LocalDate month, Long Id) {
		LocalDate startDate = month.withDayOfMonth(1);
		LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
		return namajEntryRepository.findByNamazDateBetweenAndUserId(startDate, endDate, Id);
	}

	public Optional<NamajEntry> findByNamazDateAndNamazTypeAndUserId(LocalDate date, String namazType, Long id) {
		return namajEntryRepository.findByNamazDateAndNamazTypeAndUserId(date, namazType, id);
	}

	
	public MobUserDetails getAuthenticatedUsername() {
		MobUserDetails authenticatedUsername = authUserService.getAuthenticatedUsername();
		return authenticatedUsername;

	}

	public Optional<MobUser> checkUser(Long userId) {

		return authUserService.checkUser(userId);
	}
}
