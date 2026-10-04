package com.rescuefarm.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rescuefarm.data.local.entity.CategoryCacheEntity;
import com.rescuefarm.data.local.entity.ProductBatchCacheEntity;
import com.rescuefarm.data.local.entity.ProductCacheEntity;

import java.util.List;

@Dao
public interface CatalogCacheDao {
    @Query("SELECT * FROM category_cache WHERE active = 1 ORDER BY displayOrder")
    LiveData<List<CategoryCacheEntity>> observeActiveCategories();

    @Query("SELECT * FROM product_cache ORDER BY name")
    LiveData<List<ProductCacheEntity>> observeProducts();

    @Query("SELECT * FROM product_batch_cache WHERE productId = :productId")
    LiveData<List<ProductBatchCacheEntity>> observeBatches(String productId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceCategories(List<CategoryCacheEntity> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceProducts(List<ProductCacheEntity> products);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceBatches(List<ProductBatchCacheEntity> batches);
}
