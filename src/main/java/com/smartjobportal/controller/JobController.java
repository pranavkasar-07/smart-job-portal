package com.smartjobportal.controller;
import com.smartjobportal.dto.JobRequest;
import com.smartjobportal.model.Job;
import com.smartjobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/jobs")
public class JobController {
    private final JobService service;
    public JobController(JobService service) { this.service=service; }
    @GetMapping public Page<Job> search(@RequestParam(required=false) String keyword, @RequestParam(required=false) String location,
      @RequestParam(required=false) String skills, @RequestParam(required=false) Integer minExperience,
      @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size,
      @RequestParam(defaultValue="createdAt") String sortBy, @RequestParam(defaultValue="desc") String direction) {
        return service.search(keyword,location,skills,minExperience,page,size,sortBy,direction);
    }
    @GetMapping("/recommendations") public List<Job> recommendations() { return service.recommendations(); }
    @PostMapping public ResponseEntity<Job> create(@Valid @RequestBody JobRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r)); }
    @PutMapping("/{id}") public Job update(@PathVariable Long id,@Valid @RequestBody JobRequest r) { return service.update(id,r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
