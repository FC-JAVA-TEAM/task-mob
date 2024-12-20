package com.example.application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MobRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private RoleName name;

	public MobRole() {
	}

	public MobRole(RoleName name) {
		this.name = name;
	}

	public enum RoleName {
		ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR, ROLE_GUEST
	}

	public Long getId() {
		return id;
	}

	public RoleName getName() {
		return name;
	}
}
