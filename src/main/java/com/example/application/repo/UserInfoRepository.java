package com.example.application.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.entity.UserInfo;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByName(String username);

}
