package com.work.rest.project.murza.entity.Requests;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.work.rest.project.murza.entity.ItemsDelivery;
import com.work.rest.project.murza.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "trip_requests")
public class TripRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTrip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Driver cannot be null")
    private User driver;

    // Местоположение отправки
    @NotNull(message = "Departure latitude is mandatory")
    private double departureLatitude;

    @NotNull(message = "Departure longitude is mandatory")
    private double departureLongitude;

    @NotBlank(message = "Departure address is mandatory")
    private String departureAddress;

    @NotNull(message = "Destination latitude is mandatory")
    private double destinationLatitude;

    @NotNull(message = "Destination longitude is mandatory")
    private double destinationLongitude;

    @NotBlank(message = "Destination address is mandatory")
    private String destinationAddress;

    @Positive(message = "Max weight must be positive")
    private double maxWeight;

    @Positive(message = "Max volume must be positive")
    private double maxVolume;

    @Positive(message = "Max height must be positive")
    private double maxHeight;

    @Positive(message = "Max width must be positive")
    private double maxWidth;

    @Positive(message = "Max length must be positive")
    private double maxLength;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Departure date is mandatory")
    private Date departureDate;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "Destination date is mandatory")
    private Date destinationDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "accepted_items",
            joinColumns = @JoinColumn(name = "trip_request_id"),
            inverseJoinColumns = @JoinColumn(name = "item_delivery_id")
    )
    private List<ItemsDelivery> acceptedItems;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "declined_items",
            joinColumns = @JoinColumn(name = "trip_request_id"),
            inverseJoinColumns = @JoinColumn(name = "item_delivery_id")
    )
    private List<ItemsDelivery> declinedItems;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @OneToMany(mappedBy = "tripRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<IntermediateLocation> intermediateLocations;

    private boolean isRealized;

    @CreationTimestamp
    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = true)
    private Date realizedAt;
}
