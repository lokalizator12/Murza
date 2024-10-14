package com.work.rest.project.murza.entity.Requests;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "parcel_intermediate_cities")
public class ParcelIntermediateCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parcel_request_id")
    private ParcelRequest parcelRequest;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

}