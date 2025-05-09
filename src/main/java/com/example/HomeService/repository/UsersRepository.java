package com.example.HomeService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.HomeService.model.Users;
import com.example.HomeService.model.Role;

public interface UsersRepository extends JpaRepository<Users,Long>{

	 Users findByEmail(String email);
	 Role findByRole(Role role);
	 Boolean existsByEmail(String email);
	 Boolean existsByPhoneNumber(String phoneNumber);
}