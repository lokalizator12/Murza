package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, Long> {
    boolean existsByToken(String token);
}
