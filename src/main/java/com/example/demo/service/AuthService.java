package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.response.CreateNewAccessTokenResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public UserLoginResponse login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email"));
        if(!user.getPassword().equals(password))
            throw new IllegalArgumentException("Invalid password");

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        refreshTokenService.saveRefreshToken(refreshToken, user.getId());
        return UserLoginResponse.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(user.getId()))
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public String signUp(User user){
        userRepository.save(user);
        return "Signup Successful";
    }

    public CreateNewAccessTokenResponse createNewAccessToken(String refreshToken){
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Long userId = Long.valueOf(jwtTokenProvider.getuserIdByToken(refreshToken));

        return new CreateNewAccessTokenResponse(jwtTokenProvider.generateAccessToken(userId));
    }
}
