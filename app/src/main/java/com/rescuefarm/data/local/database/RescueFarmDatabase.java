package com.rescuefarm.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rescuefarm.data.local.dao.CampaignCacheDao;
import com.rescuefarm.data.local.dao.CatalogCacheDao;
import com.rescuefarm.data.local.dao.GuestCartDao;
import com.rescuefarm.data.local.dao.PostCacheDao;
import com.rescuefarm.data.local.entity.CampaignCacheEntity;
import com.rescuefarm.data.local.entity.CategoryCacheEntity;
import com.rescuefarm.data.local.entity.GuestCartItemEntity;
import com.rescuefarm.data.local.entity.PostCacheEntity;
import com.rescuefarm.data.local.entity.ProductBatchCacheEntity;
import com.rescuefarm.data.local.entity.ProductCacheEntity;

@Database(
        entities = {
                CategoryCacheEntity.class,
                ProductCacheEntity.class,
                ProductBatchCacheEntity.class,
                CampaignCacheEntity.class,
                PostCacheEntity.class,
                GuestCartItemEntity.class
        },
        version = 1,
        exportSchema = true
)
public abstract class RescueFarmDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "rescue_farm.db";
    private static volatile RescueFarmDatabase instance;

    public static RescueFarmDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (RescueFarmDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RescueFarmDatabase.class,
                            DATABASE_NAME
                    ).build();
                }
            }
        }
        return instance;
    }

    public abstract CatalogCacheDao catalogCacheDao();
    public abstract CampaignCacheDao campaignCacheDao();
    public abstract PostCacheDao postCacheDao();
    public abstract GuestCartDao guestCartDao();
}
