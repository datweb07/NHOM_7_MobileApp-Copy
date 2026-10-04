package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.CommentStatus;

import java.util.Date;

public class Comment {
    private String id;
    private String postId;
    private String userId;
    private String content;
    private CommentStatus status;
    private Date createdAt;
    private Date updatedAt;

    public Comment() { status = CommentStatus.ACTIVE; }

    public void edit(String updatedContent, Date updateTime) {
        if (updatedContent == null || updatedContent.trim().isEmpty()) { throw new IllegalArgumentException("Comment content is required"); }
        content = updatedContent.trim();
        updatedAt = updateTime;
        status = CommentStatus.EDITED;
    }

    public void delete() {
        content = "";
        status = CommentStatus.DELETED;
    }

    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public CommentStatus getStatus() { return status; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}
