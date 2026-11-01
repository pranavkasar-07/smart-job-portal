package com.smartjobportal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="application_status_history")
@Getter @Setter @NoArgsConstructor
public class ApplicationStatusHistory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="application_id", nullable=false) private JobApplication application;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private ApplicationStatus oldStatus;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private ApplicationStatus newStatus;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="changed_by", nullable=false) private AppUser changedBy;
    @Column(length=500) private String note;
    @Column(nullable=false, updatable=false) private Instant changedAt = Instant.now();
}
