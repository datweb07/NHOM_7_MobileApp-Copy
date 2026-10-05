package com.rescuefarm.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.rescuefarm.data.local.entity.BannerCacheEntity;
import java.util.List;

@Dao
public interface BannerCacheDao {
    @Query("SELECT * FROM banner_cache WHERE active = 1 ORDER BY displayOrder, id")
    LiveData<List<BannerCacheEntity>> observeActiveBanners();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceBanners(List<BannerCacheEntity> values);
    @Query("DELETE FROM banner_cache")
    void clearBanners();
}
