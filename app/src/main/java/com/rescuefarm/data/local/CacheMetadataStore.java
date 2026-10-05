package com.rescuefarm.data.local;

import com.rescuefarm.data.local.dao.CacheMetadataDao;
import com.rescuefarm.data.local.entity.CacheMetadataEntity;
import com.rescuefarm.service.sync.CacheSyncPolicy;

public final class CacheMetadataStore {
    private final CacheMetadataDao dao;
    private final CacheSyncPolicy policy;
    public CacheMetadataStore(CacheMetadataDao dao, CacheSyncPolicy policy) {
        this.dao = dao; this.policy = policy;
    }

    public boolean shouldRefresh(String scope, long now, boolean force) {
        CacheMetadataEntity value = dao.find(scope);
        return value == null || policy.shouldRefresh(value.lastSuccessAtEpochMillis,
                value.expiresAtEpochMillis, now, force);
    }

    public void recordAttempt(String scope, long now) {
        CacheMetadataEntity value = current(scope); value.lastAttemptAtEpochMillis = now; dao.upsert(value);
    }

    public void recordSuccess(String scope, long now) {
        CacheMetadataEntity value = current(scope); value.lastAttemptAtEpochMillis = now;
        value.lastSuccessAtEpochMillis = now; value.expiresAtEpochMillis = now + policy.ttlMillis(scope);
        value.lastError = ""; dao.upsert(value);
    }

    public void recordFailure(String scope, long now, String error) {
        CacheMetadataEntity value = current(scope); value.lastAttemptAtEpochMillis = now;
        value.lastError = error == null ? "" : error; dao.upsert(value);
    }

    private CacheMetadataEntity current(String scope) {
        CacheMetadataEntity value = dao.find(scope);
        return value == null ? new CacheMetadataEntity(scope) : value;
    }
}
