package com.rescuefarm.domain;

import static org.junit.Assert.*;
import com.rescuefarm.domain.enums.CampaignStatus;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.RescueCampaign;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class RescueCampaignBoundaryTest {
    private RescueCampaign campaign(CampaignStatus status, Date locationAt) {
        Date now = new Date(1_000_000L);
        return RescueCampaign.restore("c", "s", "Giải cứu dưa", "", RescueReason.OVER_SUPPLY,
                UrgencyLevel.NORMAL, RescueMode.MOBILE_POINT,
                Collections.singletonMap("b", 100D), 100D, 0D, 0D,
                now, new Date(now.getTime() + TimeUnit.DAYS.toMillis(7)),
                10.0, 106.0, 10.1, 106.1, locationAt, true, "Điểm A", status);
    }
    @Test public void inactiveCampaign_hasNoHighlight() {
        assertEquals("", campaign(CampaignStatus.PENDING_APPROVAL, new Date(1_000_000L)).getHighlightLabel());
        assertFalse(campaign(CampaignStatus.PENDING_APPROVAL, new Date(1_000_000L)).getHighlightLabel().contains("DI ĐỘNG"));
    }
    @Test public void urgency_usesEarliestBatchExpiry() {
        RescueCampaign value = campaign(CampaignStatus.ACTIVE, new Date(1_000_000L));
        Date now = new Date(2_000_000L);
        assertEquals(UrgencyLevel.CRITICAL, value.calculateUrgency(now,
                new Date(now.getTime() + TimeUnit.DAYS.toMillis(2))));
    }
    @Test public void targetMustEqualBatchTargets() {
        RescueCampaign value = new RescueCampaign("c", "s", 100D);
        assertThrows(IllegalArgumentException.class, () -> value.defineCampaign("Campaign", "",
                RescueReason.DEMAND_DROP, RescueMode.FIXED_POINT,
                Collections.singletonMap("b", 99D)));
    }
    @Test public void exactTenMinuteLocation_isFreshButFutureIsNot() {
        Date update = new Date(1_000_000L);
        RescueCampaign value = campaign(CampaignStatus.ACTIVE, update);
        assertTrue(value.isLocationFresh(new Date(update.getTime() + TimeUnit.MINUTES.toMillis(10))));
        assertFalse(value.isLocationFresh(new Date(update.getTime() + TimeUnit.MINUTES.toMillis(10) + 1L)));
        assertFalse(value.isLocationFresh(new Date(update.getTime() - 1L)));
    }
}
