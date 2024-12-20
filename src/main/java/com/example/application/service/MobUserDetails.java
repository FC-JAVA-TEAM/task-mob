package com.example.application.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.application.entity.MobUser;

public class MobUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MobUser entity;

	public MobUserDetails() {
		super();

	}

	public MobUserDetails(MobUser entity) {

		this.entity = entity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return entity.getRoles().stream() // Fetch roles from the entity
				.map(role -> new SimpleGrantedAuthority(role.getName().name())) // Convert Role to
																				// SimpleGrantedAuthority
				.collect(Collectors.toList()); // Collect the roles as GrantedAuthority
	}

	@Override
	public String getPassword() {
		return entity.getPassword();
	}

	@Override
	public String getUsername() {
		return entity.getUsername();

	}

	public Long getUserId() {
		return entity.getId();

	}

}
