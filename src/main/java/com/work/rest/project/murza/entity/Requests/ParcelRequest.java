package com.work.rest.project.murza.entity.Requests;

import com.work.rest.project.murza.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "parcel_requests")
public class ParcelRequest {

    @Id
    @GeneratedValue
    private UUID idParcel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Sender cannot be null")
    private User sender;

    @ElementCollection
    @CollectionTable(name = "parcel_photos", joinColumns = @JoinColumn(name = "parcel_id"))
    @Column(name = "photo")
    private List<String> photos;

    @NotBlank(message = "Description is mandatory")
    private String description;

    private boolean declaration;

    @Positive(message = "Weight must be positive")
    private double weight;

    @NotBlank(message = "Size is mandatory")
    private String size;

    @Positive(message = "Price must be positive")
    private double price;

    @NotNull(message = "Pickup date is mandatory")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pickupDate;

    @NotNull(message = "Delivery date is mandatory")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryDate;

    @ManyToOne
    @NotNull(message = "Pickup location is mandatory")
    @JoinColumn(name = "pickup_location_id")
    private City pickupLocation;

    @ManyToOne
    @JoinColumn(name = "delivery_location_id")
    @NotNull(message = "Delivery location is mandatory")
    private City deliveryLocation;

    @OneToMany(mappedBy = "parcelRequest", cascade = CascadeType.ALL)
    private List<ParcelIntermediateCity> intermediateCities;

    private boolean isRealized;

    @CreationTimestamp
    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = true)
    private Date realizedAt;
}
