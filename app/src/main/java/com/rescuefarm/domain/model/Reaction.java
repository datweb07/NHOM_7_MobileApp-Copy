package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ReactionType;
import java.util.Date;

public class Reaction {
    private String id; private String postId; private String userId;
    private ReactionType type; private Date createdAt;
    public Reaction() { }
    public static Reaction restore(String id, String postId, String userId, ReactionType type,
            Date createdAt) {
        if (clean(id).isEmpty() || clean(postId).isEmpty() || clean(userId).isEmpty() || type == null) {
            throw new IllegalArgumentException("Reaction identity and type are required");
        }
        Reaction value = new Reaction(); value.id = clean(id); value.postId = clean(postId);
        value.userId = clean(userId); value.type = type;
        value.createdAt = createdAt == null ? null : new Date(createdAt.getTime()); return value;
    }
    public void changeType(ReactionType newType) {
        if (newType == null) throw new IllegalArgumentException("Reaction type is required");
        type = newType;
    }
    private static String clean(String value) { return value == null ? "" : value.trim(); }
    public String getId() { return id; } public String getPostId() { return postId; }
    public String getUserId() { return userId; } public ReactionType getType() { return type; }
    public Date getCreatedAt() { return createdAt == null ? null : new Date(createdAt.getTime()); }
}
