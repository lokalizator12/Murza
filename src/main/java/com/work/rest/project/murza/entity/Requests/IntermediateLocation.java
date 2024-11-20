package com.work.rest.project.murza.entity.Requests;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "intermediate_locations")
public class IntermediateLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Latitude is mandatory")
    private double latitude;

    @NotNull(message = "Longitude is mandatory")
    private double longitude;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @ManyToOne
    @JoinColumn(name = "trip_request_id")
    @JsonBackReference
    private TripRequest tripRequest;
}
