package com.rescuefarm.service;

import static org.junit.Assert.*;
import com.rescuefarm.domain.enums.CampaignStatus;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.RescueCampaign;
import com.rescuefarm.service.location.CampaignLocationService;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class CampaignLocationServiceTest {
    @Test public void haversine_samePointIsZero() {
        assertEquals(0D, new CampaignLocationService().distanceKilometers(
                10.762622, 106.660172, 10.762622, 106.660172), 0.000001);
    }
    @Test public void staleMobileCampaign_isExcludedFromNearby() {
        Date updated = new Date(1_000_000L);
        RescueCampaign campaign = RescueCampaign.restore("c", "s", "Mobile rescue", "",
                RescueReason.OVER_SUPPLY, UrgencyLevel.HIGH, RescueMode.MOBILE_POINT,
                Collections.singletonMap("b", 10D), 10D, 0D, 0D, updated,
                new Date(updated.getTime() + TimeUnit.DAYS.toMillis(1)), 10D, 106D,
                10D, 106D, updated, true, "", CampaignStatus.ACTIVE);
        Date stale = new Date(updated.getTime() + TimeUnit.MINUTES.toMillis(11));
        CampaignLocationService service = new CampaignLocationService();
        assertFalse(service.isEligibleForNearby(campaign, stale));
        assertTrue(Double.isNaN(service.distanceToCampaign(10D, 106D, campaign, stale)));
    }
}
