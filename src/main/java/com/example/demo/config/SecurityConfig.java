package com.example.demo.config;

import com.example.demo.security.jwt.CustomExceptionHandlerFilter;
import com.example.demo.security.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.security.jwt.JwtFilter;
import com.example.demo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new CustomExceptionHandlerFilter(), JwtFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors((corsCustomizer -> {
                    corsCustomizer.configurationSource(request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowCredentials(true);
                        config.addAllowedOrigin("*");
                        config.addAllowedHeader("*");
                        config.addAllowedMethod("*");
                        return config;
                    });
                }))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/login","/sign-up","/swagger-ui/**","/v3/api-docs/**", "/error", "/email").permitAll()
                        .anyRequest().authenticated())
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(handler ->
                        handler.authenticationEntryPoint(jwtAuthenticationEntryPoint));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
