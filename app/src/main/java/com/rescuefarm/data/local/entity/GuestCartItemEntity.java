package com.rescuefarm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "guest_cart_items")
public class GuestCartItemEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String guestId;
    public String productId;
    public String batchId;
    public String sellerId;
    public double quantity;
    public double unitPrice;
    public boolean selected;
    public long updatedAtEpochMillis;

    public GuestCartItemEntity(@NonNull String id) { this.id = id; }
}
