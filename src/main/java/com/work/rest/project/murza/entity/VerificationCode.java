package com.work.rest.project.murza.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "verification_codes")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date expirationTime;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verified = false; // Статус верификации

    @Column(nullable = false, columnDefinition = "int default 0")
    private int invalidAttempts = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date blockedUntil;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = new Date();
        }
    }
}
