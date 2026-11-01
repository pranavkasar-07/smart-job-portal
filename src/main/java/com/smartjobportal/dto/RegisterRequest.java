package com.smartjobportal.dto;
import com.smartjobportal.model.Role;
import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank @Size(max=120) String fullName,
 @NotBlank @Email @Size(max=180) String email, @NotBlank @Size(min=8,max=100) String password,
 @NotNull Role role, @Size(max=1000) String skills) {}
