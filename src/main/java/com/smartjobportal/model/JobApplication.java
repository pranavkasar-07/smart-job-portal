package com.smartjobportal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="job_applications",
  uniqueConstraints=@UniqueConstraint(name="uk_application_candidate_job", columnNames={"candidate_id","job_id"}),
  indexes=@Index(name="idx_application_status", columnList="status"))
@Getter @Setter @NoArgsConstructor
public class JobApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="candidate_id", nullable=false) private AppUser candidate;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="job_id", nullable=false) private Job job;
    @Column(nullable=false, length=500) private String resumePath;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private ApplicationStatus status = ApplicationStatus.APPLIED;
    @Column(nullable=false, updatable=false) private Instant appliedAt = Instant.now();
    @Version private Long version;
}
