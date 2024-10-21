package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.ItemsDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsDeliveryRepository extends JpaRepository<ItemsDelivery, Long> {
}