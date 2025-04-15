package com.example.HomeService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.HomeService.model.Users;
import com.example.HomeService.model.UserPrinciple;
import com.example.HomeService.repository.UsersRepository;

@Service
public class MyUserDetailService implements UserDetailsService{
    
	@Autowired
    UsersRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Users user = repo.findByEmail(email);
		
		
		System.out.println(user);
		if(user==null) {
			System.out.println("User not found with email: " +email);
			throw new UsernameNotFoundException("User not found with email: " + email);
		}
		System.out.println("sucess");
		return new UserPrinciple(user);
	}

}
