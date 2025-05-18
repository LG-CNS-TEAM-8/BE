package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>{
    List<Favorite> findByUserId(Long userId);
    
}
