package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ReactionType;

import java.util.Date;

public class Reaction {
    private String id;
    private String postId;
    private String userId;
    private ReactionType type;
    private Date createdAt;

    public Reaction() { }

    public void changeType(ReactionType newType) {
        if (newType == null) { throw new IllegalArgumentException("Reaction type is required"); }
        type = newType;
    }

    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public ReactionType getType() { return type; }
    public Date getCreatedAt() { return createdAt; }
}
