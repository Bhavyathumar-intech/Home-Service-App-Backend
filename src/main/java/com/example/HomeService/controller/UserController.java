//package com.example.HomeService.controller;
//
//import java.util.List;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.example.HomeService.model.Users;
//import com.example.HomeService.service.UserService;
//
//@RestController
//@CrossOrigin
//public class UserController {
//
//    @Autowired
//    UserService service;
//
//	@Autowired
//	HttpServletResponse response;
//
//	@PostMapping("/auth/register")
//	public Users register(@RequestBody Users user) {
//		return service.register(user);
//	}
//
//	@PostMapping("/auth/login")
//	public ResponseEntity<?> login(@RequestBody Users user) {
//		return service.verify(user, response);
//	}
//
//	@GetMapping("/auth/AllData")
//	public List<Users> getData() {
//		System.out.println("hello");
//		List<Users>users = service.getData();
//		System.out.println(users);
//		return users;
//
//}
//}
package com.example.HomeService.controller;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.HomeService.model.Users;
import com.example.HomeService.service.UserService;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService service;

    @Autowired
    HttpServletResponse response;

    @PostMapping("/auth/register")
    public Users register(@RequestBody Users user) {
        return service.register(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        ResponseEntity<?> responseEntity = service.verify(user, response);

        if (responseEntity.getBody() instanceof Map) {
            Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        }
        return responseEntity;
    }

    @GetMapping("/auth/AllData")
    public List<Users> getData() {
        return service.getData();
    }
}
