package com.example.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.application.entity.UserInfo;

@RestController
@RequestMapping("/users")
public class AddYserController {

	//@Autowired
//	UserService service;
//	@Autowired
	//ProductService service2;
	
//	 @PostMapping("/register")
//	    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
//		 service.registerUser(request.getUsername(), request.getPassword(), request.getRoles());
//	        return ResponseEntity.ok("User registered successfully!");
//	    }
	 
//	 @PostMapping("/new")
//	    public String addNewUser(@RequestBody UserInfo userInfo){
//	        return service2.addUser(userInfo);
//	    }
}
