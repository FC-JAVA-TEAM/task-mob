package com.example.application.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.application.entity.MobRole;
import com.example.application.entity.MobUser;
import com.example.application.repo.MobRoleRepository;
import com.example.application.repo.MobUserRepository;

@Service
@Primary
public class MobUserUserDetailsService implements UserDetailsService {

	@Autowired
	private MobUserRepository userRepository;
	@Autowired
	private MobRoleRepository mobRoleRepository;

	public MobUserUserDetailsService() {

	}

	public void registerUser(String username, String password, Set<String> roleNames) {
		Set<MobRole> roles = new HashSet<>();
		for (String roleName : roleNames) {
			MobRole role = mobRoleRepository.findByName(MobRole.RoleName.valueOf(roleName.toUpperCase()))
					.orElseThrow(() -> new RuntimeException("Role not found"));
			roles.add(role);
		}

		// String encodedPassword = passwordEncoder.encode(password);
		MobUser user = new MobUser(username, password, roles);
		userRepository.save(user);
	}

	// Save a new user
	public MobUser saveUser(MobUser user) {
		return userRepository.save(user);
	}

	// Find a user by name
	public Optional<MobUser> findByName(String name) {
		return userRepository.findByUsername(name);
	}

	// Find a user by id
	public Optional<MobUser> findById(Long id) {
		return userRepository.findById(id);
	}

	// Get all users
	public Iterable<MobUser> findAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<MobUser> userInfo = userRepository.findByUsername(username);

		if (userInfo.isEmpty()) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

		Set<GrantedAuthority> authorities = new HashSet<>();
		for (MobRole role : userInfo.get().getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName().name()));
		}

		return new MobUserDetails(userInfo.get());
	}
}
