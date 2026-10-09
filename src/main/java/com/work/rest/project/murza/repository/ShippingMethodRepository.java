package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    Optional<ShippingMethod> findByMethodNameIgnoreCase(String methodName);
}
