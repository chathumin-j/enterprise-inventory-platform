package com.csj.inventory.dto.mapper;

import com.csj.inventory.dto.request.RegisterRequest;
import com.csj.inventory.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(RegisterRequest request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();
    }
}
