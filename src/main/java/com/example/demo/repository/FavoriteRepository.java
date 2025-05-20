package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Favorite;
import com.example.demo.domain.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>{
    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserAndNewsLink(User user, String newsLink);
    
}
