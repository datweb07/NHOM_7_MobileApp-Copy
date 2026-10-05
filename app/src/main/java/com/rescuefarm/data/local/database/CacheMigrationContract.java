package com.rescuefarm.data.local.database;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CacheMigrationContract {
    public static final int FROM_VERSION = 5;
    public static final int TO_VERSION = 6;
    private CacheMigrationContract() { }
    public static List<String> version6Statements() {
        return Collections.unmodifiableList(Arrays.asList(
                "CREATE TABLE IF NOT EXISTS cache_metadata (scopeKey TEXT NOT NULL, lastAttemptAtEpochMillis INTEGER NOT NULL, lastSuccessAtEpochMillis INTEGER NOT NULL, expiresAtEpochMillis INTEGER NOT NULL, lastError TEXT, PRIMARY KEY(scopeKey))",
                "CREATE TABLE IF NOT EXISTS order_cache (id TEXT NOT NULL, ownerType TEXT, ownerId TEXT, orderCode TEXT, sellerId TEXT, campaignId TEXT, fulfillmentType TEXT, receiverName TEXT, receiverPhone TEXT, receiverAddress TEXT, receiverLatitude REAL NOT NULL, receiverLongitude REAL NOT NULL, subtotal REAL NOT NULL, quantityDiscount REAL NOT NULL, shippingFee REAL NOT NULL, totalAmount REAL NOT NULL, paymentMethod TEXT, paymentStatus TEXT, status TEXT, note TEXT, createdAtEpochMillis INTEGER NOT NULL, updatedAtEpochMillis INTEGER NOT NULL, cachedAtEpochMillis INTEGER NOT NULL, PRIMARY KEY(id))",
                "CREATE INDEX IF NOT EXISTS index_order_cache_ownerId ON order_cache(ownerId)",
                "CREATE INDEX IF NOT EXISTS index_order_cache_sellerId ON order_cache(sellerId)"
        ));
    }
}
