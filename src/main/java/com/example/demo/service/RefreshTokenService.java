package com.example.demo.service;

import com.example.demo.domain.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken saveRefreshToken(String refreshToken, Long userId){
        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .userId(userId)
                .build();
        RefreshToken getToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(token.getRefreshToken()))
                .orElse(token);
        return refreshTokenRepository.save(getToken);
    }
    @Transactional
    public Optional<RefreshToken> findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    @Transactional
    public Optional<RefreshToken> findByUserId(Long userId){
        return refreshTokenRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.delete(refreshToken);
    }
}
