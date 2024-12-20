package com.example.application.dto;

import java.time.LocalDate;

import com.example.application.enums.Mood;


public  class DailyMoodRequest {
	
	
    private LocalDate date;
    private Mood mood;
    private Long userId;

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

