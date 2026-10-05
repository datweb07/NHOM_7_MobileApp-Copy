package com.rescuefarm.ui.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.rescuefarm.data.repository.AuthRepository;
import com.rescuefarm.data.repository.PostRepository;
import com.rescuefarm.domain.enums.ReactionType;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.Post;
import java.util.List;

public class PostViewModel extends ViewModel {
    private static final int PAGE_SIZE = 12;
    private final PostRepository repository; private final AuthRepository authRepository;
    private final MutableLiveData<PostScreenState> state = new MutableLiveData<>(PostScreenState.idle());
    private final MutableLiveData<Boolean> hasMore = new MutableLiveData<>(true);
    private String loadedPostId = "";
    public PostViewModel(PostRepository repository, AuthRepository authRepository) {
        this.repository = repository; this.authRepository = authRepository;
    }
    public LiveData<PostScreenState> getState() { return state; }
    public LiveData<Boolean> getHasMore() { return hasMore; }
    public LiveData<List<Post>> getFeed() { return repository.observePublishedPosts(); }
    public LiveData<List<Post>> getSellerPosts() { return repository.observeSellerPosts(currentUserId()); }
    public boolean isAuthenticated() { return authRepository.isAuthenticated(); }
    public void refreshFeed() { refreshFeed(true); }
    public void loadNextPage() { if (Boolean.TRUE.equals(hasMore.getValue())) refreshFeed(false); }
    private void refreshFeed(boolean reset) {
        state.setValue(PostScreenState.loading());
        repository.refreshFeed(reset, PAGE_SIZE, new PostRepository.PageCallback() {
            @Override public void onSuccess(boolean more) { hasMore.postValue(more); state.postValue(PostScreenState.idle()); }
            @Override public void onError(PostRepository.ErrorCode error, String message) { state.postValue(PostScreenState.error(message)); }
        });
    }
    public void refreshSellerPosts() {
        String userId = requireUser("Vui lòng đăng nhập seller."); if (userId == null) return;
        repository.refreshSellerPosts(userId, action(null));
    }
    public void loadPost(String postId) {
        loadedPostId = postId == null ? "" : postId; state.setValue(PostScreenState.loading());
        repository.getPost(loadedPostId, new PostRepository.PostCallback() {
            @Override public void onSuccess(Post post) { state.postValue(PostScreenState.post(post)); }
            @Override public void onError(PostRepository.ErrorCode error, String message) { state.postValue(PostScreenState.error(message)); }
        });
    }
    public void loadEngagement() {
        if (loadedPostId.isEmpty()) return;
        repository.loadEngagement(loadedPostId, currentUserId(), new PostRepository.EngagementCallback() {
            @Override public void onSuccess(PostRepository.Engagement value) { state.postValue(PostScreenState.engagement(value)); }
            @Override public void onError(PostRepository.ErrorCode error, String message) { state.postValue(PostScreenState.error(message)); }
        });
    }
    public void savePost(String id, String campaignId, String title, String content,
            String imageUrls, String productIds, UrgencyLevel urgency, boolean submit) {
        String validation = PostValidator.validate(title, content);
        if (validation != null) { state.setValue(PostScreenState.error(validation)); return; }
        String sellerId = requireUser("Vui lòng đăng nhập seller."); if (sellerId == null) return;
        try {
            Post post = new Post(id, sellerId);
            post.defineContent(campaignId, title, content, PostValidator.parseLines(imageUrls),
                    PostValidator.parseLines(productIds), urgency);
            state.setValue(PostScreenState.loading());
            repository.savePost(post, submit, new PostRepository.PostCallback() {
                @Override public void onSuccess(Post saved) { state.postValue(PostScreenState.saved(saved,
                        submit ? "Đã gửi post chờ duyệt." : "Đã lưu bản nháp.")); }
                @Override public void onError(PostRepository.ErrorCode error, String message) { state.postValue(PostScreenState.error(message)); }
            });
        } catch (IllegalArgumentException | IllegalStateException error) {
            state.setValue(PostScreenState.error(error.getMessage()));
        }
    }
    public void toggleReaction(ReactionType type) {
        String userId = requireUser("Đăng nhập để tương tác."); if (userId == null) return;
        repository.toggleReaction(loadedPostId, userId, type, action("Đã cập nhật reaction."));
    }
    public void addComment(String content) {
        String userId = requireUser("Đăng nhập để bình luận."); if (userId == null) return;
        repository.addComment(loadedPostId, userId, content, action("Đã gửi bình luận."));
    }
    private PostRepository.ActionCallback action(String successMessage) {
        return new PostRepository.ActionCallback() {
            @Override public void onSuccess() {
                if (successMessage != null) loadEngagement();
                else state.postValue(PostScreenState.idle());
            }
            @Override public void onError(PostRepository.ErrorCode error, String message) { state.postValue(PostScreenState.error(message)); }
        };
    }
    private String currentUserId() {
        String value = authRepository.getCurrentUserId(); return value == null ? "" : value;
    }
    private String requireUser(String message) {
        if (!authRepository.isAuthenticated() || currentUserId().isEmpty()) {
            state.setValue(PostScreenState.error(message)); return null;
        }
        return currentUserId();
    }
    @Override protected void onCleared() { repository.close(); super.onCleared(); }
}
