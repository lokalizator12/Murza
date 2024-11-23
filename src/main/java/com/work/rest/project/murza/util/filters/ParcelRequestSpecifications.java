package com.work.rest.project.murza.util.filters;

import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ParcelRequestSpecifications {

    public static Specification<ParcelRequest> isNotRealized() {
        return (root, query, cb) -> cb.isFalse(root.get("isRealized"));
    }

    public static Specification<ParcelRequest> hasPickupAddress(String pickupAddress) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("pickupAddress")), "%" + pickupAddress.toLowerCase() + "%");
    }

    public static Specification<ParcelRequest> hasDeliveryAddress(String deliveryAddress) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("deliveryAddress")), "%" + deliveryAddress.toLowerCase() + "%");
    }

    public static Specification<ParcelRequest> hasPickupDateAfter(LocalDate dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("pickupDate"), dateFrom);
    }

    public static Specification<ParcelRequest> hasPickupDateBefore(LocalDate dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("pickupDate"), dateTo);
    }

    public static Specification<ParcelRequest> hasPriceGreaterThanOrEqual(Double priceMin) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), priceMin);
    }

    public static Specification<ParcelRequest> hasPriceLessThanOrEqual(Double priceMax) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), priceMax);
    }
}