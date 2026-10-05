package com.rescuefarm.data.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.rescuefarm.data.local.mapper.BannerCacheMapper;
import com.rescuefarm.domain.model.Banner;
import java.util.Date;
import org.junit.Test;

public class BannerCacheMapperTest {
    @Test public void banner_roundTrips_andUsesHalfOpenVisibilityWindow() {
        Banner source = Banner.restore("b", "Giải cứu xoài", "https://example.com/a.jpg", "c",
                2, new Date(1_000L), new Date(2_000L), true);
        Banner restored = BannerCacheMapper.toDomain(BannerCacheMapper.toEntity(source, 3_000L));
        assertEquals("c", restored.getCampaignId());
        assertTrue(restored.isVisibleAt(new Date(1_000L)));
        assertFalse(restored.isVisibleAt(new Date(2_000L)));
    }
}
