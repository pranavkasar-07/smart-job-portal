package com.smartjobportal.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record JobRequest(@NotBlank @Size(max=160) String title, @NotBlank @Size(max=120) String company,
 @NotBlank @Size(max=100) String location, @NotBlank @Size(max=4000) String description,
 @Size(max=1000) String requiredSkills, @DecimalMin("0.0") BigDecimal minSalary,
 @DecimalMin("0.0") BigDecimal maxSalary, @Min(0) Integer minExperienceYears, Boolean published) {}
