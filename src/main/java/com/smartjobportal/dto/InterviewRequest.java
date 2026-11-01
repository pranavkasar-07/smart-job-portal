package com.smartjobportal.dto;
import jakarta.validation.constraints.*;
import java.time.Instant;
public record InterviewRequest(@NotNull Long applicationId, @NotNull Instant startTime, @NotNull Instant endTime,
 @Size(max=1000) String meetingLink) {}
