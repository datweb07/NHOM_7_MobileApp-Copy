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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replacePosts(List<PostCacheEntity> posts);
}
