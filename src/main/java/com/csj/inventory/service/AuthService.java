package com.csj.inventory.service;

import com.csj.inventory.dto.request.LoginRequest;
import com.csj.inventory.dto.request.RegisterRequest;
import com.csj.inventory.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
