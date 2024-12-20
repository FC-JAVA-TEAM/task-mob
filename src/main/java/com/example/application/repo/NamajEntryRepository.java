package com.example.application.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.application.entity.NamajEntry;



@Repository
public interface NamajEntryRepository extends JpaRepository<NamajEntry, Long> {
    List<NamajEntry> findByNamazDate(LocalDate date); 
    Optional<NamajEntry> findByNamazDateAndNamazType(LocalDate date, String namazType);
    Optional<NamajEntry> findByNamazDateAndNamazTypeAndUserId(LocalDate date, String namazType,Long id);
    List<NamajEntry> findByNamazDateBetween(LocalDate startDate, LocalDate endDate);
    List<NamajEntry> findByNamazDateBetweenAndUserId(LocalDate startDate, LocalDate endDate,Long id);

    
}

