package com.work.rest.project.murza.entity.Requests;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "shipping_methods")
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String methodName;

}
