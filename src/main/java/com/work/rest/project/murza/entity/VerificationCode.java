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
    private String code; // Verification code

    @Column(nullable = false)
    private String type; // "email" or "phone"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date expirationTime; // Код истекает через, например, 10 минут

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verified = false;
}
