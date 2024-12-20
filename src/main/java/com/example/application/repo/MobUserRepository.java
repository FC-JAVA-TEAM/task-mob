package com.example.application.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.application.entity.MobUser;



@Repository
public interface MobUserRepository extends JpaRepository<MobUser, Long> {

    // Custom query method to find a User by name
    Optional<MobUser> findByUsername(String name);

}
