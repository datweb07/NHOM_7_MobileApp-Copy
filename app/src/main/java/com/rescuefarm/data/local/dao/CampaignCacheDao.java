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

    @Query("SELECT * FROM campaign_cache WHERE sellerId = :sellerId ORDER BY cachedAtEpochMillis DESC")
    LiveData<List<CampaignCacheEntity>> observeSellerCampaigns(String sellerId);

    @Query("SELECT * FROM campaign_cache WHERE id = :campaignId LIMIT 1")
    CampaignCacheEntity findCampaign(String campaignId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceCampaigns(List<CampaignCacheEntity> campaigns);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceCampaign(CampaignCacheEntity campaign);

    @Query("DELETE FROM campaign_cache WHERE status = 'ACTIVE'")
    void clearActiveCampaigns();

    @Query("DELETE FROM campaign_cache WHERE sellerId = :sellerId")
    void clearSellerCampaigns(String sellerId);
}
