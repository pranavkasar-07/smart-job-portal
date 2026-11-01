package com.smartjobportal.service;

import com.smartjobportal.dto.InterviewRequest;
import com.smartjobportal.model.*;
import com.smartjobportal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class InterviewService {
    private final InterviewRepository interviews; private final ApplicationRepository applications; private final CurrentUserService current;
    public InterviewService(InterviewRepository interviews, ApplicationRepository applications, CurrentUserService current) {
        this.interviews=interviews; this.applications=applications; this.current=current;
    }
    @Transactional
    public Interview schedule(InterviewRequest r) {
        AppUser recruiter=current.get(); if(recruiter.getRole()!=Role.RECRUITER) throw new org.springframework.security.access.AccessDeniedException("Recruiters only");
        if(!r.endTime().isAfter(r.startTime())) throw new IllegalArgumentException("Interview end time must be after start time");
        JobApplication a=applications.findByIdAndJobRecruiterId(r.applicationId(),recruiter.getId()).orElseThrow(()->new NoSuchElementException("Application not found"));
        if(a.getStatus()!=ApplicationStatus.SHORTLISTED && a.getStatus()!=ApplicationStatus.INTERVIEW_SCHEDULED)
            throw new IllegalArgumentException("Only shortlisted candidates can be scheduled");
        if(interviews.countOverlaps(a.getCandidate().getId(),r.startTime(),r.endTime())>0)
            throw new IllegalArgumentException("Candidate already has an overlapping interview");
        Interview i=new Interview(); i.setApplication(a); i.setCandidate(a.getCandidate()); i.setRecruiter(recruiter);
        i.setStartTime(r.startTime()); i.setEndTime(r.endTime()); i.setMeetingLink(r.meetingLink());
        i=interviews.save(i);
        a.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED); applications.save(a);
        return i;
    }
    @Transactional(readOnly=true)
    public List<Interview> myInterviews() {
        AppUser u=current.get();
        if(u.getRole()==Role.CANDIDATE) return interviews.findByCandidateIdOrderByStartTimeAsc(u.getId());
        return interviews.findAll().stream().filter(i->i.getRecruiter().getId().equals(u.getId())).sorted(Comparator.comparing(Interview::getStartTime)).toList();
    }
    @Transactional
    public Interview cancel(Long id) {
        AppUser u=current.get(); Interview i=interviews.findById(id).orElseThrow(()->new NoSuchElementException("Interview not found"));
        boolean allowed=(u.getRole()==Role.RECRUITER&&i.getRecruiter().getId().equals(u.getId())) ||
          (u.getRole()==Role.CANDIDATE&&i.getCandidate().getId().equals(u.getId()));
        if(!allowed) throw new org.springframework.security.access.AccessDeniedException("Not your interview");
        i.setCancelled(true); return interviews.save(i);
    }
}
