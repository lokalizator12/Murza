package com.work.rest.project.murza.entity.Requests;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "trip_intermediate_cities")
public class TripIntermediateCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_request_id")
    private TripRequest tripRequest;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

}