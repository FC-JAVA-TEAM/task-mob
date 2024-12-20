package com.example.application.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.entity.MobRole;

public interface  MobRoleRepository extends JpaRepository<MobRole, Long> {

    // Custom query method to find a User by name
    Optional<MobRole> findByName(MobRole.RoleName name);

}