package com.work.rest.project.murza.repository;


import com.work.rest.project.murza.entity.Role;
import com.work.rest.project.murza.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);

}
