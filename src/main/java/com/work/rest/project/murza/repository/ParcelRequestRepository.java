package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParcelRequestRepository extends JpaRepository<ParcelRequest, UUID> {


    @Query(value = "SELECT p from ParcelRequest p where p.isRealized = ?1")
    List<ParcelRequest> findAllByRealized(Boolean realized);
}
