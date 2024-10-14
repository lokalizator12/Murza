package com.work.rest.project.murza.entity.Requests;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "country")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}