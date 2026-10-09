package com.monisom.controller;

import com.monisom.dto.LoginRequest;
import com.monisom.dto.LoginResponse;
import com.monisom.entity.SchoolAdmin;
import com.monisom.repository.SchoolAdminRepository;
import com.monisom.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SchoolAdminRepository repository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          SchoolAdminRepository repository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        SchoolAdmin admin = repository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
        String token = jwtService.generateToken(admin.getEmail(), admin.getSchoolId());
        return new LoginResponse(token, admin.getName(), admin.getSchoolId(), admin.getEmail());
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("email", authentication.getName());
    }
}
