package com.rescuefarm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_cache")
public class CategoryCacheEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String imageUrl;
    public boolean active;
    public int displayOrder;
    public long cachedAtEpochMillis;

    public CategoryCacheEntity(@NonNull String id) { this.id = id; }
}
