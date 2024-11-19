package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {

    @Query(value = "SELECT t FROM TripRequest t where t.isRealized = ?1")
    public List<TripRequest> findAllByRealized(Boolean realized);

    public Page<TripRequest> findAllByDriver(User driver, Pageable pageable);

    public Page<TripRequest> findAllByIsRealized(Boolean realized, Pageable pageable);
}
