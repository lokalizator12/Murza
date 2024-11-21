package com.work.rest.project.murza.repository;


import com.work.rest.project.murza.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}

