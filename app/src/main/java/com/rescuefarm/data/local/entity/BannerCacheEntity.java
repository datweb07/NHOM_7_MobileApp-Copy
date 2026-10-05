package com.rescuefarm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "banner_cache")
public class BannerCacheEntity {
    @PrimaryKey @NonNull public String id;
    public String title;
    public String imageUrl;
    public String campaignId;
    public int displayOrder;
    public long startAtEpochMillis;
    public long endAtEpochMillis;
    public boolean active;
    public long cachedAtEpochMillis;
    public BannerCacheEntity(@NonNull String id) { this.id = id; }
}
