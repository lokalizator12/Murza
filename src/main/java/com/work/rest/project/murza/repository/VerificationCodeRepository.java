package com.work.rest.project.murza.repository;


import com.work.rest.project.murza.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;


@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {


    @Query(value = "SELECT * FROM verification_codes vc " +
            "WHERE vc.user_id = :userId " +
            "AND vc.type = :type " +
            "AND vc.verified = false " +
            "AND vc.expiration_time >= NOW() - INTERVAL 2 MINUTE " +
            "ORDER BY vc.expiration_time DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<VerificationCode> findLatestUnverifiedCodeWithinTimeframeNative(
            @Param("userId") Long userId,
            @Param("type") String type);

    @Query(value = "SELECT COUNT(*) FROM verification_codes vc " +
            "WHERE vc.user_id = :userId " +
            "AND vc.type = :type " +
            "AND vc.created_at >= :timeLimit", nativeQuery = true)
    int countRecentAttempts(@Param("userId") Long userId,
                            @Param("type") String type,
                            @Param("timeLimit") Date timeLimit);

    @Query(value = "SELECT * FROM verification_codes vc " +
            "WHERE vc.user_id = :userId " +
            "AND vc.type = :type " +
            "AND vc.invalid_attempts >= 3 " +
            "AND vc.blocked_until >= NOW()", nativeQuery = true)
    Optional<VerificationCode> findBlockedCode(@Param("userId") Long userId,
                                               @Param("type") String type);

    @Query(value = "SELECT * FROM verification_codes vc " +
            "WHERE vc.user_id = :userId " +
            "AND vc.type = :type " +
            "AND vc.verified = false " +
            "AND vc.expiration_time >= NOW() " +
            "AND (vc.blocked_until IS NULL OR vc.blocked_until < NOW()) " +
            "ORDER BY vc.expiration_time DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<VerificationCode> findActiveCode(@Param("userId") Long userId, @Param("type") String type);

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.user.id = :userId AND vc.type = :type ORDER BY vc.createdAt DESC LIMIT 1")
    Optional<VerificationCode> findLatestCode(@Param("userId") Long userId, @Param("type") String type);

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.code = :token AND vc.type = :type ORDER BY vc.createdAt DESC LIMIT 1")
    Optional<VerificationCode> findByCodeAndType(@Param("token") String token, @Param("type") String type);

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.code = :code AND vc.verified = false")
    Optional<VerificationCode> findByCodeAndNotVerified(@Param("code") String code);


    @Query("SELECT vc.user.id FROM VerificationCode vc WHERE vc.code = :verificationCode")
    Optional<Long> findUserIdByCode(@Param("verificationCode") String verificationCode);
}

