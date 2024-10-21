package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
}
