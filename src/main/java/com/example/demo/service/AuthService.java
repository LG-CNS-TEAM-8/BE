package com.example.demo.service;

import com.example.demo.common.exception.CustomException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.domain.RefreshToken;
import com.example.demo.domain.User;
import com.example.demo.dto.response.CheckEmailResponse;
import com.example.demo.dto.response.CreateNewAccessTokenResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.dto.response.UserSignUpResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserLoginResponse login(UserLoginRequest body){
        String email = body.getEmail();
        String password = body.getPassword();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!passwordEncoder.matches(password, user.getPassword()))
            throw new CustomException(ErrorCode.INVALID_PASSWORD);

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        refreshTokenService.saveRefreshToken(refreshToken, user.getId());
        return UserLoginResponse.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(user.getId()))
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public UserSignUpResponse signUp(User user){
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return new UserSignUpResponse(user.getId());
    }

    @Transactional
    public CheckEmailResponse isEmailExist(String email){
        return new CheckEmailResponse(userRepository.existsByEmail(email));
    }

    @Transactional
    public void deleteRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenService.findByRefreshToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    public CreateNewAccessTokenResponse createNewAccessToken(String refreshToken){
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Long userId = Long.valueOf(jwtTokenProvider.getuserIdByToken(refreshToken));

        return new CreateNewAccessTokenResponse(jwtTokenProvider.generateAccessToken(userId));
    }
}
