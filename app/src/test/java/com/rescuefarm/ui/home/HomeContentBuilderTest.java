package com.rescuefarm.ui.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.rescuefarm.domain.enums.CampaignStatus;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.RescueCampaign;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class HomeContentBuilderTest {
    @Test public void sectionOrder_hasExactlyNineFinalSections() {
        assertEquals(9, HomeViewState.SECTION_ORDER.size());
        assertEquals(HomeViewState.Section.BANNERS, HomeViewState.SECTION_ORDER.get(0));
        assertEquals(HomeViewState.Section.RESCUE_FEED, HomeViewState.SECTION_ORDER.get(8));
    }

    @Test public void criticalIsFirst_andStaleMobileIsExcludedFromNearby() {
        Date now = new Date(2_000_000L);
        RescueCampaign normal = campaign("normal", UrgencyLevel.NORMAL, RescueMode.FIXED_POINT,
                now, null, false);
        RescueCampaign critical = campaign("critical", UrgencyLevel.CRITICAL, RescueMode.FIXED_POINT,
                now, null, false);
        RescueCampaign stale = campaign("stale", UrgencyLevel.HIGH, RescueMode.MOBILE_POINT,
                now, new Date(now.getTime() - TimeUnit.MINUTES.toMillis(11)), true);
        HomeViewState state = new HomeContentBuilder().build(Collections.emptyList(),
                Collections.emptyList(), Arrays.asList(normal, stale, critical),
                Collections.emptyList(), 10D, 106D, now,
                HomeViewState.LocationState.AVAILABLE, false, "");
        assertEquals("critical", state.getActiveCampaigns().get(0).getId());
        assertEquals(1, state.getCritical().size());
        assertTrue(state.getNearbyMobile().isEmpty());
        assertEquals(2, state.getNearbyFixed().size());
    }

    @Test public void cachedContentIsMarkedStaleUntilRefreshSucceeds() {
        HomeViewState state = new HomeContentBuilder().build(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                null, null, new Date(), HomeViewState.LocationState.PERMISSION_REQUIRED,
                false, "cache");
        assertEquals(HomeViewState.DataFreshness.STALE, state.getDataFreshness());
    }

    private RescueCampaign campaign(String id, UrgencyLevel urgency, RescueMode mode,
            Date now, Date locationAt, boolean sharing) {
        return RescueCampaign.restore(id, "seller", "Chiến dịch " + id, "Mô tả",
                RescueReason.OVER_SUPPLY, urgency, mode, Collections.singletonMap("batch", 10D),
                10D, 0D, 0D, new Date(now.getTime() - 1000L),
                new Date(now.getTime() + TimeUnit.DAYS.toMillis(2)), 10D, 106D,
                10D, 106D, locationAt, sharing, "TP HCM", CampaignStatus.ACTIVE);
    }
}
