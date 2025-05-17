package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Interest;

public interface InterestRepository extends JpaRepository<Interest, Long>{
    Optional<Interest> findByName(String name);
    
}
