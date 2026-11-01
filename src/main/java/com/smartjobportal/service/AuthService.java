package com.smartjobportal.service;

import com.smartjobportal.dto.*;
import com.smartjobportal.model.*;
import com.smartjobportal.repository.UserRepository;
import com.smartjobportal.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder;
    private final AuthenticationManager authManager; private final JwtService jwtService;
    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwtService) {
        this.users=users; this.encoder=encoder; this.authManager=authManager; this.jwtService=jwtService;
    }
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (users.existsByEmail(req.email().toLowerCase())) throw new IllegalArgumentException("Email is already registered");
        AppUser u = new AppUser(); u.setFullName(req.fullName().trim()); u.setEmail(req.email().toLowerCase().trim());
        u.setPasswordHash(encoder.encode(req.password())); u.setRole(req.role()); u.setSkills(req.skills());
        u = users.save(u);
        var principal = User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles(u.getRole().name()).build();
        return new AuthResponse(jwtService.generateToken(principal), u.getId(), u.getFullName(), u.getEmail(), u.getRole().name());
    }
    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email().toLowerCase().trim(), req.password()));
        AppUser u = users.findByEmail(req.email().toLowerCase().trim()).orElseThrow();
        var principal = User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles(u.getRole().name()).build();
        return new AuthResponse(jwtService.generateToken(principal), u.getId(), u.getFullName(), u.getEmail(), u.getRole().name());
    }
}
