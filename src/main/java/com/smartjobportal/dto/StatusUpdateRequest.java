package com.smartjobportal.dto;
import com.smartjobportal.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record StatusUpdateRequest(@NotNull ApplicationStatus status, @Size(max=500) String note) {}
