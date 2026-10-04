package com.rescuefarm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product_cache")
public class ProductCacheEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String sellerId;
    public String categoryId;
    public String name;
    public double originalPrice;
    public double rescuePrice;
    public String unit;
    public String status;
    public long cachedAtEpochMillis;

    public ProductCacheEntity(@NonNull String id) { this.id = id; }
}
