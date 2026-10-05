package com.rescuefarm.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.rescuefarm.data.local.entity.CacheMetadataEntity;

@Dao
public interface CacheMetadataDao {
    @Query("SELECT * FROM cache_metadata WHERE scopeKey = :scopeKey LIMIT 1")
    CacheMetadataEntity find(String scopeKey);

    @Query("SELECT * FROM cache_metadata WHERE scopeKey = :scopeKey LIMIT 1")
    LiveData<CacheMetadataEntity> observe(String scopeKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(CacheMetadataEntity metadata);
}
