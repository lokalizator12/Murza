package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParcelRequestRepository extends JpaRepository<ParcelRequest, UUID> {
}
