package com.rescuefarm.service.location;

import com.rescuefarm.domain.enums.CampaignStatus;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.model.RescueCampaign;
import java.util.Date;

public final class CampaignLocationService {
    private static final double EARTH_RADIUS_KM = 6371.0088;

    public double distanceKilometers(double fromLatitude, double fromLongitude,
            double toLatitude, double toLongitude) {
        validate(fromLatitude, fromLongitude);
        validate(toLatitude, toLongitude);
        double lat1 = Math.toRadians(fromLatitude);
        double lat2 = Math.toRadians(toLatitude);
        double deltaLat = Math.toRadians(toLatitude - fromLatitude);
        double deltaLng = Math.toRadians(toLongitude - fromLongitude);
        double a = Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLng / 2.0) * Math.sin(deltaLng / 2.0);
        return EARTH_RADIUS_KM * 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
    }

    public boolean isEligibleForNearby(RescueCampaign campaign, Date now) {
        return campaign != null && campaign.getStatus() == CampaignStatus.ACTIVE
                && campaign.getRescueMode() == RescueMode.MOBILE_POINT
                && campaign.isLocationFresh(now);
    }

    public double distanceToCampaign(double latitude, double longitude,
            RescueCampaign campaign, Date now) {
        if (!isEligibleForNearby(campaign, now)) { return Double.NaN; }
        return distanceKilometers(latitude, longitude,
                campaign.getCurrentLatitude(), campaign.getCurrentLongitude());
    }

    private static void validate(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || latitude < -90.0 || latitude > 90.0
                || !Double.isFinite(longitude) || longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Invalid latitude or longitude");
        }
    }
}
