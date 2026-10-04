package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ShipmentStatus;

public class Shipment {
    private String id;
    private String orderId;
    private String carrierName;
    private String trackingCode;
    private ShipmentStatus status;
    private double distanceKm;
    private double shippingFee;

    public Shipment() { status = ShipmentStatus.PENDING; }

    public void updateStatus(ShipmentStatus updatedStatus) {
        if (updatedStatus == null) { throw new IllegalArgumentException("Shipment status is required"); }
        status = updatedStatus;
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getCarrierName() { return carrierName; }
    public String getTrackingCode() { return trackingCode; }
    public ShipmentStatus getStatus() { return status; }
    public double getDistanceKm() { return distanceKm; }
    public double getShippingFee() { return shippingFee; }
}
