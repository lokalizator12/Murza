package com.work.rest.project.murza.util.filters;

import com.work.rest.project.murza.entity.Requests.TripRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TripRequestSpecifications {
    public static Specification<TripRequest> isNotRealized() {
        return (root, query, cb) -> cb.isFalse(root.get("isRealized"));
    }

    public static Specification<TripRequest> hasDepartureAddress(String departureAddress) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("departureAddress")), "%" + departureAddress.toLowerCase() + "%");
    }

    public static Specification<TripRequest> hasDestinationAddress(String destinationAddress) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("destinationAddress")), "%" + destinationAddress.toLowerCase() + "%");
    }

    public static Specification<TripRequest> hasDepartureDateAfter(LocalDate dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("departureDate").as(LocalDate.class), dateFrom);
    }

    public static Specification<TripRequest> hasDepartureDateBefore(LocalDate dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("departureDate").as(LocalDate.class), dateTo);
    }
}

