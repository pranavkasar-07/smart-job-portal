package com.smartjobportal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(name="uk_users_email", columnNames="email"))
@Getter @Setter @NoArgsConstructor
public class AppUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String fullName;
    @Column(nullable=false, unique=true, length=180) private String email;
    @Column(nullable=false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private Role role;
    @Column(length=1000) private String skills;
    @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
}
