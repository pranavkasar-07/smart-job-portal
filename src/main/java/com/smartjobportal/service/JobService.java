package com.smartjobportal.service;

import com.smartjobportal.dto.JobRequest;
import com.smartjobportal.model.*;
import com.smartjobportal.repository.JobRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.util.*;

@Service
public class JobService {
    private final JobRepository jobs; private final CurrentUserService current;
    public JobService(JobRepository jobs, CurrentUserService current) { this.jobs=jobs; this.current=current; }
    @Transactional
    public Job create(JobRequest r) {
        AppUser recruiter=current.get(); requireRecruiter(recruiter);
        Job j=new Job(); apply(j,r); j.setRecruiter(recruiter);
        return jobs.save(j);
    }
    @Transactional
    public Job update(Long id, JobRequest r) {
        AppUser recruiter=current.get(); requireRecruiter(recruiter);
        Job j=jobs.findById(id).orElseThrow(() -> new NoSuchElementException("Job not found"));
        if (!j.getRecruiter().getId().equals(recruiter.getId())) throw new org.springframework.security.access.AccessDeniedException("You do not own this job");
        apply(j,r); return jobs.save(j);
    }
    @Transactional
    public void delete(Long id) {
        AppUser recruiter=current.get(); requireRecruiter(recruiter);
        Job j=jobs.findById(id).orElseThrow(() -> new NoSuchElementException("Job not found"));
        if (!j.getRecruiter().getId().equals(recruiter.getId())) throw new org.springframework.security.access.AccessDeniedException("You do not own this job");
        jobs.delete(j);
    }
    public Page<Job> search(String keyword, String location, String skills, Integer minExperience, int page, int size, String sortBy, String direction) {
        int safeSize=Math.min(Math.max(size,1),50);
        Sort sort=Sort.by("desc".equalsIgnoreCase(direction)?Sort.Direction.DESC:Sort.Direction.ASC,
                Set.of("createdAt","title","minSalary","location").contains(sortBy)?sortBy:"createdAt");
        Specification<Job> spec=(root,q,cb)-> {
            List<Predicate> p=new ArrayList<>(); p.add(cb.isTrue(root.get("published")));
            if(keyword!=null&&!keyword.isBlank()) p.add(cb.or(cb.like(cb.lower(root.get("title")),"%"+keyword.toLowerCase()+"%"),
                    cb.like(cb.lower(root.get("description")),"%"+keyword.toLowerCase()+"%")));
            if(location!=null&&!location.isBlank()) p.add(cb.like(cb.lower(root.get("location")),"%"+location.toLowerCase()+"%"));
            if(skills!=null&&!skills.isBlank()) p.add(cb.like(cb.lower(root.get("requiredSkills")),"%"+skills.toLowerCase()+"%"));
            if(minExperience!=null) p.add(cb.lessThanOrEqualTo(root.get("minExperienceYears"),minExperience));
            return cb.and(p.toArray(new Predicate[0]));
        };
        return jobs.findAll(spec,PageRequest.of(Math.max(page,0),safeSize,sort));
    }
    public List<Job> recommendations() {
        AppUser candidate=current.get();
        if(candidate.getRole()!=Role.CANDIDATE) throw new org.springframework.security.access.AccessDeniedException("Candidates only");
        if(candidate.getSkills()==null||candidate.getSkills().isBlank()) return List.of();
        return jobs.findAll().stream().filter(Job::isPublished)
          .filter(j->j.getRequiredSkills()!=null && Arrays.stream(candidate.getSkills().toLowerCase().split("[,; ]+"))
            .filter(s->s.length()>1).anyMatch(s->j.getRequiredSkills().toLowerCase().contains(s))).limit(10).toList();
    }
    private void apply(Job j, JobRequest r) {
        if(r.minSalary()!=null && r.maxSalary()!=null && r.minSalary().compareTo(r.maxSalary())>0)
            throw new IllegalArgumentException("Minimum salary cannot exceed maximum salary");
        j.setTitle(r.title()); j.setCompany(r.company()); j.setLocation(r.location()); j.setDescription(r.description());
        j.setRequiredSkills(r.requiredSkills()); j.setMinSalary(r.minSalary()); j.setMaxSalary(r.maxSalary());
        j.setMinExperienceYears(r.minExperienceYears()); j.setPublished(Boolean.TRUE.equals(r.published()));
    }
    private void requireRecruiter(AppUser u) { if(u.getRole()!=Role.RECRUITER) throw new org.springframework.security.access.AccessDeniedException("Recruiters only"); }
}
