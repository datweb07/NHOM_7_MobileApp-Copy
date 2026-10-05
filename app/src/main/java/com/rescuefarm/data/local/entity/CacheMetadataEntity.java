package com.rescuefarm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cache_metadata")
public class CacheMetadataEntity {
    @PrimaryKey @NonNull public String scopeKey;
    public long lastAttemptAtEpochMillis;
    public long lastSuccessAtEpochMillis;
    public long expiresAtEpochMillis;
    public String lastError;

    public CacheMetadataEntity(@NonNull String scopeKey) { this.scopeKey = scopeKey; }
}
