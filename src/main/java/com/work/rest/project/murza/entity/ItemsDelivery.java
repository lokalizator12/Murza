package com.work.rest.project.murza.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ItemsDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String itemName;

    @Column
    private String description;

}
