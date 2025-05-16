package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.UserLoginResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Transactional
    public UserLoginResponse login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email"));
        if(!user.getPassword().equals(password))
            throw new IllegalArgumentException("Invalid password");
        return UserLoginResponse.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(user.getId()))
                .build();
    }

    @Transactional
    public String signUp(User user){
        userRepository.save(user);
        return "Signup Successful";
    }
}
