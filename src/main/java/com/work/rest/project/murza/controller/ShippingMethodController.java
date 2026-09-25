package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import com.work.rest.project.murza.repository.ShippingMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-methods")
@RequiredArgsConstructor
public class ShippingMethodController {
    private final ShippingMethodRepository repository;

    @GetMapping
    public ResponseEntity<List<ShippingMethod>> list() {
        return ResponseEntity.ok(repository.findAll());
    }
}
