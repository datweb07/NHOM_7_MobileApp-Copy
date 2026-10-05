package com.rescuefarm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post_cache")
public class PostCacheEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String sellerId;
    public String campaignId;
    public String title;
    public String content;
    public String imageUrlsSerialized;
    public String linkedProductIdsSerialized;
    public String urgencyLevel;
    public String status;
    public long createdAtEpochMillis;
    public long viewCount;
    public long cachedAtEpochMillis;

    public PostCacheEntity(@NonNull String id) { this.id = id; }
}
