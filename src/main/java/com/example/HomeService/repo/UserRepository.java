package com.example.HomeService.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.HomeService.model.Users;
import com.example.HomeService.model.Role;

public interface UserRepository extends JpaRepository<Users,Long>{

	 Users findByEmail(String email);
	 Role findByRole(Role role);
}