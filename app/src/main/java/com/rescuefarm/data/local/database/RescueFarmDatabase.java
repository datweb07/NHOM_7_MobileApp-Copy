package com.rescuefarm.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;

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
        version = 3,
        exportSchema = true
)
public abstract class RescueFarmDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "rescue_farm.db";
    private static volatile RescueFarmDatabase instance;
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE product_cache ADD COLUMN description TEXT");
            database.execSQL("ALTER TABLE product_cache ADD COLUMN origin TEXT");
            database.execSQL("ALTER TABLE product_cache ADD COLUMN province TEXT");
            database.execSQL("ALTER TABLE product_cache ADD COLUMN imageUrlsSerialized TEXT");
            database.execSQL("ALTER TABLE product_cache ADD COLUMN averageRating REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE product_cache ADD COLUMN reviewCount INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE product_batch_cache ADD COLUMN inventoryVersion INTEGER NOT NULL DEFAULT 0");
        }
    };
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE campaign_cache ADD COLUMN description TEXT");
            database.execSQL("ALTER TABLE campaign_cache ADD COLUMN batchTargetsSerialized TEXT");
            database.execSQL("ALTER TABLE campaign_cache ADD COLUMN startAtEpochMillis INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE campaign_cache ADD COLUMN endAtEpochMillis INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE campaign_cache ADD COLUMN latitude REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE campaign_cache ADD COLUMN longitude REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE campaign_cache ADD COLUMN locationName TEXT");
        }
    };

    public static RescueFarmDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (RescueFarmDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RescueFarmDatabase.class,
                            DATABASE_NAME
                    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build();
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
