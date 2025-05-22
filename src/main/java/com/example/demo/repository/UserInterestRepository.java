package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Interest;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInterest;
import com.example.demo.domain.UserInterestId;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId>{
    List<UserInterest> findByUserId(Long userId);

    boolean existsByUserAndInterest(User user, Interest interest);
    void deleteByUserAndInterest(User user, Interest interest);

    void deleteByUser(User user);
}
