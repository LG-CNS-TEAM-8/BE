package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.common.exception.CustomException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.domain.Interest;
import com.example.demo.domain.User;
import com.example.demo.domain.UserInterest;
import com.example.demo.dto.request.InterestRequestDto;
import com.example.demo.repository.InterestRepository;
import com.example.demo.repository.UserInterestRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterestService {
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final UserInterestRepository userInterestRepository;

    @Transactional
    public void addInterest(InterestRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Interest interest = interestRepository.findByName(dto.getName())
                .orElseGet(() -> interestRepository.save(Interest.builder().name(dto.getName()).build()));

        if(userInterestRepository.existsByUserAndInterest(user,interest)){
            throw new CustomException(ErrorCode.DUPLICATE_INTEREST);
        }
        
        UserInterest mapping = UserInterest.builder()
            .user(user)
            .interest(interest)
            .build();
            
        userInterestRepository.save(mapping);
    }

    @Transactional
    public void deleteInterest(InterestRequestDto dto){
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        Interest interest = interestRepository.findByName(dto.getName())
            .orElseThrow(()->new CustomException((ErrorCode.INTEREST_NOT_FOUND)));
        
        if(!userInterestRepository.existsByUserAndInterest(user,interest)){
            throw new CustomException(ErrorCode.INTEREST_NOT_FOUND_FOR_USER);
        }
            
        userInterestRepository.deleteByUserAndInterest(user, interest);;
    }
    
}
