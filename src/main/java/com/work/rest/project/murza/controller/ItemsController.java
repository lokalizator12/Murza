package com.work.rest.project.murza.controller;


import com.work.rest.project.murza.entity.ItemsDelivery;
import com.work.rest.project.murza.service.ItemsDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items-delivery")
public class ItemsController {
    private final ItemsDeliveryService itemsDeliveryService;

    @GetMapping
    private ResponseEntity<List<ItemsDelivery>> getAllItems() {
        log.info("Request to getting all items");
        return ResponseEntity.ok(itemsDeliveryService.getAllItems());
    }
}
