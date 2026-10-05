package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.rescuefarm.data.local.dao.PostCacheDao;
import com.rescuefarm.data.local.entity.PostCacheEntity;
import com.rescuefarm.data.local.mapper.PostCacheMapper;
import com.rescuefarm.domain.enums.ReactionType;
import com.rescuefarm.domain.model.Post;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class OfflinePostRepository implements PostRepository {
    private static final String MESSAGE = "Đang offline hoặc Firebase chưa cấu hình; thao tác ghi feed không khả dụng.";
    private final PostCacheDao dao; private final ExecutorService executor;
    public OfflinePostRepository(PostCacheDao dao, ExecutorService executor) {
        this.dao = dao; this.executor = executor;
    }
    @Override public LiveData<List<Post>> observePublishedPosts() {
        return Transformations.map(dao.observePublishedPosts(), PostCacheMapper::posts);
    }
    @Override public LiveData<List<Post>> observeSellerPosts(String sellerId) {
        return Transformations.map(dao.observeSellerPosts(sellerId), PostCacheMapper::posts);
    }
    @Override public void refreshFeed(boolean reset, int pageSize, PageCallback callback) {
        callback.onError(ErrorCode.NOT_CONFIGURED, MESSAGE);
    }
    @Override public void refreshSellerPosts(String sellerId, ActionCallback callback) { unavailable(callback); }
    @Override public void getPost(String postId, PostCallback callback) {
        executor.execute(() -> {
            PostCacheEntity cached = dao.findPost(postId);
            if (cached == null) callback.onError(ErrorCode.NOT_FOUND, "Post chưa có trong cache.");
            else try { callback.onSuccess(PostCacheMapper.toDomain(cached)); }
            catch (IllegalArgumentException error) { callback.onError(ErrorCode.NOT_FOUND, "Cache post không hợp lệ."); }
        });
    }
    @Override public void savePost(Post post, boolean submit, PostCallback callback) {
        callback.onError(ErrorCode.NOT_CONFIGURED, MESSAGE);
    }
    @Override public void loadEngagement(String postId, String userId, EngagementCallback callback) {
        callback.onError(ErrorCode.NOT_CONFIGURED, "Reaction và comment cần kết nối mạng.");
    }
    @Override public void toggleReaction(String postId, String userId, ReactionType type,
            ActionCallback callback) { unavailable(callback); }
    @Override public void addComment(String postId, String userId, String content,
            ActionCallback callback) { unavailable(callback); }
    private void unavailable(ActionCallback callback) { callback.onError(ErrorCode.NOT_CONFIGURED, MESSAGE); }
    @Override public void close() { executor.shutdownNow(); }
}
