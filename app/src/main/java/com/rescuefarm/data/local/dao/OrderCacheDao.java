package com.rescuefarm.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.rescuefarm.data.local.entity.OrderCacheEntity;
import java.util.List;

@Dao
public interface OrderCacheDao {
    @Query("SELECT * FROM order_cache WHERE ownerId = :ownerId ORDER BY createdAtEpochMillis DESC")
    LiveData<List<OrderCacheEntity>> observeOwnerOrders(String ownerId);

    @Query("SELECT * FROM order_cache WHERE sellerId = :sellerId ORDER BY createdAtEpochMillis DESC")
    LiveData<List<OrderCacheEntity>> observeSellerOrders(String sellerId);

    @Query("SELECT * FROM order_cache WHERE id = :orderId LIMIT 1")
    OrderCacheEntity find(String orderId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceOrders(List<OrderCacheEntity> orders);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replaceOrder(OrderCacheEntity order);

    @Query("DELETE FROM order_cache WHERE ownerId = :ownerId")
    void clearOwnerOrders(String ownerId);

    @Query("DELETE FROM order_cache WHERE sellerId = :sellerId")
    void clearSellerOrders(String sellerId);

    @Query("DELETE FROM order_cache WHERE cachedAtEpochMillis < :cutoffEpochMillis")
    void deleteOlderThan(long cutoffEpochMillis);
}
