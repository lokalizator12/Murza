package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.entity.ItemsDelivery;
import com.work.rest.project.murza.repository.ItemsDeliveryRepository;
import com.work.rest.project.murza.service.ItemsDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemsDeliveryServiceImpl implements ItemsDeliveryService {

    private final ItemsDeliveryRepository itemsDeliveryRepository;
    @Override
    public List<ItemsDelivery> getAllItems() {

        return itemsDeliveryRepository.findAll();
    }
}
