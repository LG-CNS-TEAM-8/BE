package com.example.demo.service;

import com.example.demo.common.exception.CustomException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.domain.User;
import com.example.demo.dto.response.UserInfoResponse;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserInfoResponse getUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Transactional
    public String deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
        return "User Deleted";
    }
}
