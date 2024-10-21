package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Requests.TripRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {
}
