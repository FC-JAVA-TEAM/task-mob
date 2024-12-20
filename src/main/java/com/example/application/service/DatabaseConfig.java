package com.example.application.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseConfig {
    @Value("${SPRING_DATASOURCE_URL}")
    private String dbUrl;

    @Value("${SPRING_DATASOURCE_USERNAME}")
    private String dbUsername;

    @Value("${SPRING_DATASOURCE_PASSWORD}")
    private String dbPassword;

    @PostConstruct
    public void logDbConfig() {
        System.out.println("Database URL: " + dbUrl); // This should be avoided in production
        System.out.println("Database Username: " + dbUsername); // This should be avoided in production
    }
}