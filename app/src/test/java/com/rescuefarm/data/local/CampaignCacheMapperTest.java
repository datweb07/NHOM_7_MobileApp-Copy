package com.rescuefarm.data.local;

import static org.junit.Assert.*;
import com.rescuefarm.data.local.entity.CampaignCacheEntity;
import com.rescuefarm.data.local.mapper.CampaignCacheMapper;
import com.rescuefarm.domain.enums.CampaignStatus;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.RescueCampaign;
import java.util.Collections;
import java.util.Date;
import org.junit.Test;

public class CampaignCacheMapperTest {
    @Test public void campaign_roundTripsThroughRoomEntity() {
        RescueCampaign source = RescueCampaign.restore("c", "s", "Giải cứu xoài", "Mô tả",
                RescueReason.NEAR_EXPIRY, UrgencyLevel.CRITICAL, RescueMode.FIXED_POINT,
                Collections.singletonMap("b", 25D), 25D, 2D, 5D,
                new Date(1_000L), new Date(2_000L), 10D, 106D, 0D, 0D,
                null, false, "Chợ A", CampaignStatus.ACTIVE);
        CampaignCacheEntity entity = CampaignCacheMapper.toEntity(source, 3_000L);
        RescueCampaign restored = CampaignCacheMapper.toDomain(entity);
        assertEquals(source.getBatchTargets(), restored.getBatchTargets());
        assertEquals(20D, restored.calculateProgress(), 0.000001);
        assertEquals("Chợ A", restored.getLocationName());
    }
}
