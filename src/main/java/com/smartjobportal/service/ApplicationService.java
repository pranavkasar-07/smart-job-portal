package com.smartjobportal.service;

import com.smartjobportal.dto.StatusUpdateRequest;
import com.smartjobportal.model.*;
import com.smartjobportal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class ApplicationService {
    private final ApplicationRepository applications; private final JobRepository jobs; private final StatusHistoryRepository history;
    private final CurrentUserService current; private final Path resumeDir;
    public ApplicationService(ApplicationRepository applications, JobRepository jobs, StatusHistoryRepository history,
        CurrentUserService current, org.springframework.core.env.Environment env) {
        this.applications=applications; this.jobs=jobs; this.history=history; this.current=current;
        this.resumeDir=Paths.get(env.getProperty("app.resume-dir","uploads/resumes")).toAbsolutePath().normalize();
    }
    @Transactional
    public JobApplication apply(Long jobId, MultipartFile resume) throws IOException {
        AppUser candidate=current.get();
        if(candidate.getRole()!=Role.CANDIDATE) throw new org.springframework.security.access.AccessDeniedException("Candidates only");
        Job job=jobs.findById(jobId).orElseThrow(()->new NoSuchElementException("Job not found"));
        if(!job.isPublished()) throw new IllegalArgumentException("Job is not accepting applications");
        if(resume==null||resume.isEmpty()) throw new IllegalArgumentException("Resume file is required");
        String original=Objects.requireNonNullElse(resume.getOriginalFilename(),"resume.pdf");
        String ext=original.contains(".")?original.substring(original.lastIndexOf('.')).toLowerCase():"";
        if(!Set.of(".pdf",".doc",".docx").contains(ext)) throw new IllegalArgumentException("Resume must be PDF, DOC, or DOCX");
        if(resume.getSize()>5*1024*1024) throw new IllegalArgumentException("Resume must be 5MB or smaller");
        if(applications.existsByCandidateIdAndJobId(candidate.getId(),jobId)) throw new IllegalArgumentException("You have already applied to this job");
        Files.createDirectories(resumeDir);
        String safeName=UUID.randomUUID()+ext; Path target=resumeDir.resolve(safeName).normalize();
        if(!target.startsWith(resumeDir)) throw new IllegalArgumentException("Invalid filename");
        resume.transferTo(target);
        try {
            JobApplication a=new JobApplication(); a.setCandidate(candidate); a.setJob(job); a.setResumePath(target.toString());
            a=applications.saveAndFlush(a);
            ApplicationStatusHistory h=new ApplicationStatusHistory(); h.setApplication(a); h.setOldStatus(ApplicationStatus.APPLIED);
            h.setNewStatus(ApplicationStatus.APPLIED); h.setChangedBy(candidate); h.setNote("Application submitted"); history.save(h);
            return a;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            try { Files.deleteIfExists(target); } catch(IOException ignored) {}
            throw new IllegalArgumentException("You have already applied to this job");
        }
    }
    @Transactional(readOnly=true)
    public List<JobApplication> myApplications() {
        AppUser u=current.get(); if(u.getRole()!=Role.CANDIDATE) throw new org.springframework.security.access.AccessDeniedException("Candidates only");
        return applications.findByCandidateIdOrderByAppliedAtDesc(u.getId());
    }
    @Transactional(readOnly=true)
    public List<JobApplication> jobApplications(Long jobId) {
        AppUser u=current.get(); if(u.getRole()!=Role.RECRUITER) throw new org.springframework.security.access.AccessDeniedException("Recruiters only");
        Job j=jobs.findById(jobId).orElseThrow(()->new NoSuchElementException("Job not found"));
        if(!j.getRecruiter().getId().equals(u.getId())) throw new org.springframework.security.access.AccessDeniedException("You do not own this job");
        return applications.findByJobIdAndJobRecruiterIdOrderByAppliedAtDesc(jobId,u.getId());
    }
    @Transactional
    public JobApplication updateStatus(Long applicationId, StatusUpdateRequest req) {
        AppUser recruiter=current.get(); if(recruiter.getRole()!=Role.RECRUITER) throw new org.springframework.security.access.AccessDeniedException("Recruiters only");
        JobApplication a=applications.findByIdAndJobRecruiterId(applicationId,recruiter.getId()).orElseThrow(()->new NoSuchElementException("Application not found"));
        ApplicationStatus old=a.getStatus(), next=req.status();
        if(!allowed(old,next)) throw new IllegalArgumentException("Invalid status transition: "+old+" -> "+next);
        a.setStatus(next); applications.save(a);
        ApplicationStatusHistory h=new ApplicationStatusHistory(); h.setApplication(a); h.setOldStatus(old); h.setNewStatus(next); h.setChangedBy(recruiter); h.setNote(req.note()); history.save(h);
        return a;
    }
    @Transactional(readOnly=true)
    public List<ApplicationStatusHistory> history(Long id) {
        AppUser u=current.get();
        JobApplication a=applications.findById(id).orElseThrow(()->new NoSuchElementException("Application not found"));
        if(u.getRole()==Role.CANDIDATE&&!a.getCandidate().getId().equals(u.getId())) throw new org.springframework.security.access.AccessDeniedException("Not your application");
        if(u.getRole()==Role.RECRUITER&&!a.getJob().getRecruiter().getId().equals(u.getId())) throw new org.springframework.security.access.AccessDeniedException("Not your job application");
        return history.findByApplicationIdOrderByChangedAtAsc(id);
    }
    private boolean allowed(ApplicationStatus old, ApplicationStatus next) {
        if(old==next) return false;
        return switch(old) {
            case APPLIED -> next==ApplicationStatus.SHORTLISTED || next==ApplicationStatus.REJECTED;
            case SHORTLISTED -> next==ApplicationStatus.INTERVIEW_SCHEDULED || next==ApplicationStatus.REJECTED;
            case INTERVIEW_SCHEDULED -> next==ApplicationStatus.SELECTED || next==ApplicationStatus.REJECTED || next==ApplicationStatus.SHORTLISTED;
            case SELECTED, REJECTED -> false;
        };
    }
}
