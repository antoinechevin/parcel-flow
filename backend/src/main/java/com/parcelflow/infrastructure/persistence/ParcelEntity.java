package com.parcelflow.infrastructure.persistence;

import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.ParcelStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "parcels")
public class ParcelEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String trackingNumber;

    private String carrier;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private ParcelStatus status;

    private String pickupPointId;
    private String pickupPointName;
    private String pickupPointAddress;
    private String pickupPointOpeningHours;

    private String pickupCode;
    private String qrCodeUrl;

    @Enumerated(EnumType.STRING)
    private BarcodeType barcodeType;

    public ParcelEntity() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public ParcelStatus getStatus() { return status; }
    public void setStatus(ParcelStatus status) { this.status = status; }

    public String getPickupPointId() { return pickupPointId; }
    public void setPickupPointId(String pickupPointId) { this.pickupPointId = pickupPointId; }

    public String getPickupPointName() { return pickupPointName; }
    public void setPickupPointName(String pickupPointName) { this.pickupPointName = pickupPointName; }

    public String getPickupPointAddress() { return pickupPointAddress; }
    public void setPickupPointAddress(String pickupPointAddress) { this.pickupPointAddress = pickupPointAddress; }

    public String getPickupPointOpeningHours() { return pickupPointOpeningHours; }
    public void setPickupPointOpeningHours(String pickupPointOpeningHours) { this.pickupPointOpeningHours = pickupPointOpeningHours; }

    public String getPickupCode() { return pickupCode; }
    public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public BarcodeType getBarcodeType() { return barcodeType; }
    public void setBarcodeType(BarcodeType barcodeType) { this.barcodeType = barcodeType; }
}
