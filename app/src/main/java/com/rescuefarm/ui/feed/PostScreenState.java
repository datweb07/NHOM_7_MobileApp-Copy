package com.rescuefarm.ui.feed;

import com.rescuefarm.data.repository.PostRepository;
import com.rescuefarm.domain.model.Post;

public final class PostScreenState {
    public enum Status { IDLE, LOADING, POST, SAVED, ENGAGEMENT, ERROR }
    private final Status status; private final Post post;
    private final PostRepository.Engagement engagement; private final String message;
    private PostScreenState(Status status, Post post, PostRepository.Engagement engagement, String message) {
        this.status = status; this.post = post; this.engagement = engagement; this.message = message;
    }
    public static PostScreenState idle() { return value(Status.IDLE, null); }
    public static PostScreenState loading() { return value(Status.LOADING, null); }
    public static PostScreenState post(Post value) { return new PostScreenState(Status.POST, value, null, null); }
    public static PostScreenState saved(Post value, String message) { return new PostScreenState(Status.SAVED, value, null, message); }
    public static PostScreenState engagement(PostRepository.Engagement value) { return new PostScreenState(Status.ENGAGEMENT, null, value, null); }
    public static PostScreenState error(String message) { return value(Status.ERROR, message); }
    private static PostScreenState value(Status status, String message) {
        return new PostScreenState(status, null, null, message);
    }
    public Status getStatus() { return status; } public Post getPost() { return post; }
    public PostRepository.Engagement getEngagement() { return engagement; }
    public String getMessage() { return message; }
}
