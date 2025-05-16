package com.example.demo.security.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveRefreshToken((HttpServletRequest) request);

        if(token != null) {
            if (token.startsWith("Bearer ")) token = token.substring(7);

            if (((HttpServletRequest) request).getRequestURI()
                    .equals("/members/token/refresh") && jwtTokenProvider.validateRefreshToken(token)) {
                Authentication authentication = jwtTokenProvider.getRefreshAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            token =  jwtTokenProvider.resolveToken((HttpServletRequest) request);
            if (token != null) {
                if (token.startsWith("Bearer ")) token = token.substring(7);

                if (jwtTokenProvider.validateToken(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
