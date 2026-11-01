package com.smartjobportal.controller;
import com.smartjobportal.dto.InterviewRequest;
import com.smartjobportal.model.Interview;
import com.smartjobportal.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/interviews")
public class InterviewController {
    private final InterviewService service;
    public InterviewController(InterviewService service) { this.service=service; }
    @PostMapping public ResponseEntity<Interview> schedule(@Valid @RequestBody InterviewRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(service.schedule(r)); }
    @GetMapping("/me") public List<Interview> mine() { return service.myInterviews(); }
    @PatchMapping("/{id}/cancel") public Interview cancel(@PathVariable Long id) { return service.cancel(id); }
}
