package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Role;
import com.work.rest.project.murza.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByRole(Role role);

    Optional<User> findByEmail(String email);
}
