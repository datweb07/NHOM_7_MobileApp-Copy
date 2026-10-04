package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.model.Post;

import java.util.List;

public interface PostRepository {
    LiveData<List<Post>> observePublishedPosts();
}
