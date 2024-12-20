package com.example.application.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.application.entity.MobUser;
import com.example.application.exception.ResourceNotFoundException;
import com.example.application.repo.MobUserRepository;

@Component
public class AuthUserService {
	
	
	@Autowired
    private MobUserRepository mobUserRepository;
	
	 public MobUserDetails getAuthenticatedUsername() {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	        // Check if the authentication object is not null and the user is authenticated
	        if (authentication != null && authentication.isAuthenticated()) {
	            // Further check if the user is not anonymous
	            if (!(authentication.getPrincipal() instanceof MobUserDetails)) {
	                // If the user is anonymous (for example, the user might be using a 'guest' or 'anonymous' role)
	                return null; // or return a default value like "anonymous" or throw an exception
	            }

	            // If the user is authenticated and not anonymous, return their username
	            return (MobUserDetails) authentication.getPrincipal(); // This will return the username of the authenticated user
	        }
	        // Return null if the user is not authenticated
	        return null;
	    }
	 
	 public Optional<MobUser> checkUser(Long userId) {
	        Optional<MobUser> userOptional = mobUserRepository.findById(userId);
	        if (userOptional.isEmpty()) {
	            throw new ResourceNotFoundException("User not found with ID: " + userId);
	        }
	        return userOptional;
	    }

}
