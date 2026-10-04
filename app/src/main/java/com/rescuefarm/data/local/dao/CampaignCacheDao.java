package com.rescuefarm.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rescuefarm.data.local.entity.CampaignCacheEntity;

import java.util.List;

@Dao
public interface CampaignCacheDao {
    @Query("SELECT * FROM campaign_cache WHERE status = 'ACTIVE' ORDER BY cachedAtEpochMillis DESC")
    LiveData<List<CampaignCacheEntity>> observeActiveCampaigns();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceCampaigns(List<CampaignCacheEntity> campaigns);
}
