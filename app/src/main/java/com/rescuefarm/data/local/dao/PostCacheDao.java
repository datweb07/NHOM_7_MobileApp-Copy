package com.rescuefarm.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rescuefarm.data.local.entity.PostCacheEntity;

import java.util.List;

@Dao
public interface PostCacheDao {
    @Query("SELECT * FROM post_cache WHERE status = 'PUBLISHED' ORDER BY createdAtEpochMillis DESC")
    LiveData<List<PostCacheEntity>> observePublishedPosts();

    @Query("SELECT * FROM post_cache WHERE sellerId = :sellerId ORDER BY createdAtEpochMillis DESC")
    LiveData<List<PostCacheEntity>> observeSellerPosts(String sellerId);

    @Query("SELECT * FROM post_cache WHERE id = :postId LIMIT 1")
    PostCacheEntity findPost(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replacePosts(List<PostCacheEntity> posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replacePost(PostCacheEntity post);

    @Query("DELETE FROM post_cache WHERE status = 'PUBLISHED'")
    void clearPublishedPosts();

    @Query("DELETE FROM post_cache WHERE sellerId = :sellerId")
    void clearSellerPosts(String sellerId);
}
