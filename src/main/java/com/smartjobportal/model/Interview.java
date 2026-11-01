package com.smartjobportal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="interviews", indexes=@Index(name="idx_interview_candidate_time", columnList="candidate_id,start_time"))
@Getter @Setter @NoArgsConstructor
public class Interview {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="application_id", nullable=false) private JobApplication application;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="candidate_id", nullable=false) private AppUser candidate;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="recruiter_id", nullable=false) private AppUser recruiter;
    @Column(name="start_time", nullable=false) private Instant startTime;
    @Column(name="end_time", nullable=false) private Instant endTime;
    @Column(length=1000) private String meetingLink;
    @Column(nullable=false) private boolean cancelled = false;
    @Column(nullable=false) private Instant createdAt = Instant.now();
}
