package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.CampaignStatus;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.enums.UrgencyLevel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RescueCampaign {
    public static final long MOBILE_LOCATION_FRESHNESS_MINUTES = 10L;
    private static final double QUANTITY_TOLERANCE = 0.000001;

    private String id;
    private String sellerId;
    private String title;
    private String description;
    private RescueReason rescueReason;
    private UrgencyLevel urgencyLevel;
    private RescueMode rescueMode;
    private Map<String, Double> batchTargets;
    private double targetQuantity;
    private double reservedQuantity;
    private double rescuedQuantity;
    private Date startDate;
    private Date endDate;
    private double latitude;
    private double longitude;
    private double currentLatitude;
    private double currentLongitude;
    private Date locationUpdatedAt;
    private boolean locationSharingEnabled;
    private String locationName;
    private CampaignStatus status;

    public RescueCampaign() {
        batchTargets = new HashMap<>();
        status = CampaignStatus.DRAFT;
        urgencyLevel = UrgencyLevel.NORMAL;
    }

    public RescueCampaign(String id, String sellerId, double targetQuantity) {
        this();
        requirePositive(targetQuantity, "Target quantity");
        this.id = id;
        this.sellerId = sellerId;
        this.targetQuantity = targetQuantity;
    }

    public void defineCampaign(
            String title,
            String description,
            RescueReason rescueReason,
            RescueMode rescueMode,
            Map<String, Double> batchTargets
    ) {
        if (rescueReason == null || rescueMode == null || batchTargets == null || batchTargets.isEmpty()) {
            throw new IllegalArgumentException("Campaign reason, mode and batch targets are required");
        }
        double totalBatchTarget = 0.0;
        for (double batchTarget : batchTargets.values()) {
            requirePositive(batchTarget, "Batch target");
            totalBatchTarget += batchTarget;
        }
        if (Math.abs(targetQuantity - totalBatchTarget) > QUANTITY_TOLERANCE) {
            throw new IllegalArgumentException("Batch targets must equal the campaign target quantity");
        }
        this.title = title;
        this.description = description;
        this.rescueReason = rescueReason;
        this.rescueMode = rescueMode;
        this.batchTargets = new HashMap<>(batchTargets);
    }

    public double calculateProgress() {
        return targetQuantity <= 0.0 ? 0.0 : rescuedQuantity / targetQuantity * 100.0;
    }

    public double calculateRemainingQuantity() {
        return Math.max(0.0, targetQuantity - reservedQuantity - rescuedQuantity);
    }

    public UrgencyLevel calculateUrgency(Date now, Date earliestBatchExpiryDate) {
        long campaignDaysRemaining = daysBetween(now, endDate);
        long batchDaysRemaining = daysBetween(now, earliestBatchExpiryDate);
        double remainingRatio = targetQuantity <= 0.0 ? 0.0 : calculateRemainingQuantity() / targetQuantity;

        if (batchDaysRemaining <= 2L || (campaignDaysRemaining <= 1L && remainingRatio > 0.30)) {
            urgencyLevel = UrgencyLevel.CRITICAL;
        } else if (batchDaysRemaining <= 5L || campaignDaysRemaining <= 3L) {
            urgencyLevel = UrgencyLevel.HIGH;
        } else {
            urgencyLevel = UrgencyLevel.NORMAL;
        }
        return urgencyLevel;
    }

    public void schedule(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !endDate.after(startDate)) {
            throw new IllegalArgumentException("Campaign end date must be after its start date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getHighlightLabel() {
        if (rescueMode == RescueMode.MOBILE_POINT) { return "ĐIỂM GIẢI CỨU DI ĐỘNG"; }
        if (urgencyLevel == UrgencyLevel.CRITICAL) { return "CẦN GIẢI CỨU GẤP"; }
        if (urgencyLevel == UrgencyLevel.HIGH) { return "CẦN GIẢI CỨU SỚM"; }
        return "ĐANG GIẢI CỨU";
    }

    public void reserveQuantity(double quantity) {
        requirePositive(quantity, "Reserved quantity");
        if (calculateRemainingQuantity() + QUANTITY_TOLERANCE < quantity) { throw new IllegalStateException("Campaign target does not have enough remaining quantity"); }
        reservedQuantity += quantity;
    }

    public void commitRescue(double quantity) {
        requirePositive(quantity, "Rescued quantity");
        if (reservedQuantity + QUANTITY_TOLERANCE < quantity) { throw new IllegalStateException("Campaign does not have enough reserved quantity"); }
        reservedQuantity -= quantity;
        rescuedQuantity += quantity;
    }

    public void releaseReservation(double quantity) {
        requirePositive(quantity, "Released quantity");
        if (reservedQuantity + QUANTITY_TOLERANCE < quantity) { throw new IllegalStateException("Campaign does not have enough reserved quantity"); }
        reservedQuantity -= quantity;
    }

    public void updateCurrentLocation(double latitude, double longitude, Date updatedAt) {
        if (rescueMode != RescueMode.MOBILE_POINT) { throw new IllegalStateException("Only a mobile rescue campaign can update its current location"); }
        validateCoordinates(latitude, longitude);
        currentLatitude = latitude;
        currentLongitude = longitude;
        locationUpdatedAt = updatedAt;
    }

    public boolean isLocationFresh(Date now) {
        if (rescueMode != RescueMode.MOBILE_POINT || !locationSharingEnabled || locationUpdatedAt == null || now.before(locationUpdatedAt)) { return false; }
        long ageMinutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - locationUpdatedAt.getTime());
        return ageMinutes <= MOBILE_LOCATION_FRESHNESS_MINUTES;
    }

    public void setLocationSharingEnabled(boolean enabled) {
        if (enabled && rescueMode != RescueMode.MOBILE_POINT) {
            throw new IllegalStateException("Location sharing is only available for mobile rescue campaigns");
        }
        locationSharingEnabled = enabled;
    }

    public void submitForApproval() {
        if (rescueReason == null || batchTargets == null || batchTargets.isEmpty()) { throw new IllegalStateException("Campaign requires a rescue reason and at least one batch target"); }
        status = CampaignStatus.PENDING_APPROVAL;
    }

    private static void validateCoordinates(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || latitude < -90.0 || latitude > 90.0 || !Double.isFinite(longitude) || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Invalid latitude or longitude");
        }
    }

    private static void requirePositive(double quantity, String fieldName) {
        if (!Double.isFinite(quantity) || quantity <= 0.0) { throw new IllegalArgumentException(fieldName + " must be positive"); }
    }

    private static long daysBetween(Date start, Date end) {
        if (start == null || end == null) { return Long.MAX_VALUE; }
        return TimeUnit.MILLISECONDS.toDays(end.getTime() - start.getTime());
    }

    public String getId() { return id; }
    public String getSellerId() { return sellerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public RescueReason getRescueReason() { return rescueReason; }
    public UrgencyLevel getUrgencyLevel() { return urgencyLevel; }
    public RescueMode getRescueMode() { return rescueMode; }
    public Map<String, Double> getBatchTargets() { return new HashMap<>(batchTargets); }
    public double getTargetQuantity() { return targetQuantity; }
    public double getReservedQuantity() { return reservedQuantity; }
    public double getRescuedQuantity() { return rescuedQuantity; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getCurrentLatitude() { return currentLatitude; }
    public double getCurrentLongitude() { return currentLongitude; }
    public Date getLocationUpdatedAt() { return locationUpdatedAt; }
    public boolean isLocationSharingEnabled() { return locationSharingEnabled; }
    public String getLocationName() { return locationName; }
    public CampaignStatus getStatus() { return status; }
}
