package com.example.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.application.entity.UserInfo;
import com.example.application.repo.UserInfoRepository;

@Service
public class ProductService {

	@Autowired
	private UserInfoRepository repository;

	public String addUser(UserInfo userInfo) {
		userInfo.setPassword(userInfo.getPassword());
		repository.save(userInfo);
		return "user added to system ";
	}
}
