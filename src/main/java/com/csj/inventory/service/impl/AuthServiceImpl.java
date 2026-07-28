package com.csj.inventory.service.impl;

import com.csj.inventory.dto.mapper.UserMapper;
import com.csj.inventory.dto.request.LoginRequest;
import com.csj.inventory.dto.request.RegisterRequest;
import com.csj.inventory.dto.response.AuthResponse;
import com.csj.inventory.entity.User;
import com.csj.inventory.exception.DuplicateResourceException;
import com.csj.inventory.exception.InvalidCredentialsException;
import com.csj.inventory.logging.StructuredLogger;
import com.csj.inventory.repository.UserRepository;
import com.csj.inventory.security.JwtService;
import com.csj.inventory.security.SecurityUser;
import com.csj.inventory.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final StructuredLogger structuredLogger;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = UserMapper.toEntity(request, passwordEncoder);
        User saved = userRepository.save(user);

        structuredLogger.logEvent(saved.getUsername(), "REGISTER", "User", saved.getId().toString(), "SUCCESS", null);

        SecurityUser securityUser = new SecurityUser(saved);
        String token = jwtService.generateToken(securityUser);
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(saved.getUsername())
                .role(saved.getRole().name())
                .expiresInMs(jwtService.getAccessTokenExpirationMs())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        long start = System.currentTimeMillis();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            structuredLogger.logLoginAttempt(request.getUsername(), false, System.currentTimeMillis() - start);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        SecurityUser securityUser = new SecurityUser(user);
        String token = jwtService.generateToken(securityUser);

        structuredLogger.logLoginAttempt(request.getUsername(), true, System.currentTimeMillis() - start);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .expiresInMs(jwtService.getAccessTokenExpirationMs())
                .build();
    }
}
