package com.rescuefarm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product_batch_cache")
public class ProductBatchCacheEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String productId;
    public String activeCampaignId;
    public long harvestAtEpochMillis;
    public long expiresAtEpochMillis;
    public double initialQuantity;
    public double availableQuantity;
    public double reservedQuantity;
    public double soldQuantity;
    public String status;
    public long cachedAtEpochMillis;

    public ProductBatchCacheEntity(@NonNull String id) { this.id = id; }
}
