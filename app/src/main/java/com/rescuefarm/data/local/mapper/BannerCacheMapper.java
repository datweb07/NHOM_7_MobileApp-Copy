package com.rescuefarm.data.local.mapper;

import com.rescuefarm.data.local.entity.BannerCacheEntity;
import com.rescuefarm.domain.model.Banner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class BannerCacheMapper {
    private BannerCacheMapper() { }
    public static BannerCacheEntity toEntity(Banner value, long cachedAt) {
        BannerCacheEntity entity = new BannerCacheEntity(value.getId());
        entity.title = value.getTitle(); entity.imageUrl = value.getImageUrl();
        entity.campaignId = value.getCampaignId(); entity.displayOrder = value.getDisplayOrder();
        entity.startAtEpochMillis = value.getStartDate().getTime();
        entity.endAtEpochMillis = value.getEndDate().getTime(); entity.active = value.isActive();
        entity.cachedAtEpochMillis = cachedAt; return entity;
    }
    public static Banner toDomain(BannerCacheEntity value) {
        return Banner.restore(value.id, value.title, value.imageUrl, value.campaignId,
                value.displayOrder, new Date(value.startAtEpochMillis),
                new Date(value.endAtEpochMillis), value.active);
    }
    public static List<Banner> banners(List<BannerCacheEntity> values) {
        List<Banner> result = new ArrayList<>();
        if (values != null) for (BannerCacheEntity value : values) {
            try { result.add(toDomain(value)); } catch (IllegalArgumentException ignored) { }
        }
        return result;
    }
}
