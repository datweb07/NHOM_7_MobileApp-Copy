package com.rescuefarm.domain;

import static org.junit.Assert.assertEquals;

import com.rescuefarm.domain.model.RescueCampaign;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;

import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RescueCampaignTest {
    private static final double DELTA = 0.000001;

    @Test
    public void pendingOrder_doesNotIncreaseRescuedQuantity() {
        RescueCampaign campaign = new RescueCampaign("campaign-1", "seller-1", 100.0);

        campaign.reserveQuantity(25.0);

        assertEquals(25.0, campaign.getReservedQuantity(), DELTA);
        assertEquals(0.0, campaign.getRescuedQuantity(), DELTA);
        assertEquals(0.0, campaign.calculateProgress(), DELTA);
    }

    @Test
    public void deliveredOrder_movesReservedQuantityToRescuedQuantity() {
        RescueCampaign campaign = new RescueCampaign("campaign-1", "seller-1", 100.0);
        campaign.reserveQuantity(25.0);

        campaign.commitRescue(25.0);

        assertEquals(0.0, campaign.getReservedQuantity(), DELTA);
        assertEquals(25.0, campaign.getRescuedQuantity(), DELTA);
        assertEquals(25.0, campaign.calculateProgress(), DELTA);
    }

    @Test
    public void mobileLocation_isFreshForAtMostTenMinutes() {
        RescueCampaign campaign = new RescueCampaign("campaign-1", "seller-1", 100.0);
        campaign.defineCampaign(
                "Mobile rescue",
                "Fresh produce on the road",
                RescueReason.OVER_SUPPLY,
                RescueMode.MOBILE_POINT,
                Collections.singletonMap("batch-1", 100.0)
        );
        campaign.setLocationSharingEnabled(true);
        Date updateTime = new Date(1_000_000L);
        campaign.updateCurrentLocation(10.762622, 106.660172, updateTime);

        Date exactlyTenMinutesLater = new Date(
                updateTime.getTime() + TimeUnit.MINUTES.toMillis(10L)
        );
        Date moreThanTenMinutesLater = new Date(
                updateTime.getTime() + TimeUnit.MINUTES.toMillis(11L)
        );

        org.junit.Assert.assertTrue(campaign.isLocationFresh(exactlyTenMinutesLater));
        org.junit.Assert.assertFalse(campaign.isLocationFresh(moreThanTenMinutesLater));
    }
}
