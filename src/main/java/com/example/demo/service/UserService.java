package com.example.demo.service;

import com.example.demo.common.exception.CustomException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.domain.User;
import com.example.demo.dto.request.InterestRequestDto;
import com.example.demo.dto.response.UserInfoDto;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.UserInterestRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InterestService interestService;
    private final UserInterestRepository userInterestRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public UserInfoDto getUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserInfoDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserInfoDto updateUserInfo(UserInfoDto request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.update(request.getEmail(), request.getName());
        userRepository.save(user);
        userInterestRepository.deleteByUser(user);

        List<String> interests = request.getInterests();
        InterestRequestDto dto = new InterestRequestDto();
        dto.setUserId(user.getId());
        dto.setName(interests);
        interestService.addInterest(dto);

        return UserInfoDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .interests(interests)
                .build();
    }

    @Transactional
    public String deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        favoriteRepository.deleteByUser(user);
        userInterestRepository.deleteByUser(user);
        userRepository.delete(user);
        return "User Deleted";
    }
}
