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

    @Query("SELECT * FROM product_cache WHERE status = 'ACTIVE' ORDER BY name")
    LiveData<List<ProductCacheEntity>> observeActiveProducts();

    @Query("SELECT * FROM product_cache WHERE sellerId = :sellerId ORDER BY name")
    LiveData<List<ProductCacheEntity>> observeSellerProducts(String sellerId);

    @Query("SELECT * FROM product_batch_cache WHERE productId = :productId")
    LiveData<List<ProductBatchCacheEntity>> observeBatches(String productId);

    @Query("SELECT * FROM product_cache WHERE id = :productId LIMIT 1")
    ProductCacheEntity findProduct(String productId);

    @Query("SELECT * FROM product_batch_cache WHERE id = :batchId LIMIT 1")
    ProductBatchCacheEntity findBatch(String batchId);

    @Query("SELECT * FROM product_batch_cache WHERE productId = :productId")
    List<ProductBatchCacheEntity> findBatches(String productId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceCategories(List<CategoryCacheEntity> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceProducts(List<ProductCacheEntity> products);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceBatches(List<ProductBatchCacheEntity> batches);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceProduct(ProductCacheEntity product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceBatch(ProductBatchCacheEntity batch);

    @Query("DELETE FROM category_cache")
    void clearCategories();

    @Query("DELETE FROM product_cache WHERE sellerId = :sellerId")
    void clearSellerProducts(String sellerId);

    @Query("DELETE FROM product_cache WHERE status = 'ACTIVE'")
    void clearActiveProducts();

    @Query("DELETE FROM product_batch_cache WHERE productId = :productId")
    void clearBatches(String productId);

    @Query("DELETE FROM product_cache WHERE id = :productId")
    void deleteProduct(String productId);

    @Query("DELETE FROM product_batch_cache WHERE id = :batchId")
    void deleteBatch(String batchId);
}
