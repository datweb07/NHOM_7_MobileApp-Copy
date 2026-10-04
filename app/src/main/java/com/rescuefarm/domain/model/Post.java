package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.PostStatus;
import com.rescuefarm.domain.enums.UrgencyLevel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private String id;
    private String sellerId;
    private String campaignId;
    private String title;
    private String content;
    private List<String> imageUrls;
    private List<String> linkedProductIds;
    private UrgencyLevel urgencyLevel;
    private PostStatus status;
    private long viewCount;
    private Date createdAt;

    public Post() {
        imageUrls = new ArrayList<>();
        linkedProductIds = new ArrayList<>();
        status = PostStatus.DRAFT;
    }

    public void submitForApproval() {
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            throw new IllegalStateException("Post title and content are required");
        }
        status = PostStatus.PENDING_APPROVAL;
    }

    public void increaseView() { viewCount++; }
    public String getId() { return id; }
    public String getSellerId() { return sellerId; }
    public String getCampaignId() { return campaignId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getImageUrls() { return new ArrayList<>(imageUrls); }
    public List<String> getLinkedProductIds() { return new ArrayList<>(linkedProductIds); }
    public UrgencyLevel getUrgencyLevel() { return urgencyLevel; }
    public PostStatus getStatus() { return status; }
    public long getViewCount() { return viewCount; }
    public Date getCreatedAt() { return createdAt; }
}
