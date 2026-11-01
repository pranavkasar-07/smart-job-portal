package com.smartjobportal.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="jobs", indexes={@Index(name="idx_jobs_location", columnList="location"), @Index(name="idx_jobs_published", columnList="published")})
@Getter @Setter @NoArgsConstructor
public class Job {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=160) private String title;
    @Column(nullable=false, length=120) private String company;
    @Column(nullable=false, length=100) private String location;
    @Column(nullable=false, length=4000) private String description;
    @Column(length=1000) private String requiredSkills;
    @Column(precision=12, scale=2) private BigDecimal minSalary;
    @Column(precision=12, scale=2) private BigDecimal maxSalary;
    private Integer minExperienceYears;
    @Column(nullable=false) private boolean published = false;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="recruiter_id", nullable=false) private AppUser recruiter;
    @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
    @Version private Long version;
}
