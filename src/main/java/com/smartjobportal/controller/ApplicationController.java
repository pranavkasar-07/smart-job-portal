package com.smartjobportal.controller;
import com.smartjobportal.dto.StatusUpdateRequest;
import com.smartjobportal.model.*;
import com.smartjobportal.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
@RestController @RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService service;
    public ApplicationController(ApplicationService service) { this.service=service; }
    @PostMapping("/{jobId}") public ResponseEntity<JobApplication> apply(@PathVariable Long jobId,@RequestParam("resume") MultipartFile resume) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.apply(jobId,resume));
    }
    @GetMapping("/me") public List<JobApplication> mine() { return service.myApplications(); }
    @GetMapping("/job/{jobId}") public List<JobApplication> forJob(@PathVariable Long jobId) { return service.jobApplications(jobId); }
    @PatchMapping("/{applicationId}/status") public JobApplication status(@PathVariable Long applicationId,@Valid @RequestBody StatusUpdateRequest r) { return service.updateStatus(applicationId,r); }
    @GetMapping("/{applicationId}/history") public List<ApplicationStatusHistory> history(@PathVariable Long applicationId) { return service.history(applicationId); }
}
