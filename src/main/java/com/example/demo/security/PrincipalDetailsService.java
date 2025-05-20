package com.example.demo.security;

import com.example.demo.common.exception.CustomException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new PrincipalDetails(userEntity);
    }
}
