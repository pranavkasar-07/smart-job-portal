package com.smartjobportal.controller;
import com.smartjobportal.dto.*;
import com.smartjobportal.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth=auth; }
    @PostMapping("/register") public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(auth.register(r)); }
    @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r) { return auth.login(r); }
}
