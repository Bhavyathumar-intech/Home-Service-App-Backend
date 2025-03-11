package com.example.HomeService.repo;

import com.example.HomeService.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

    Optional<UserDetails> findByUserId(Long userId);
    Optional<UserDetails> findByUser_Id(Long userId);

}
