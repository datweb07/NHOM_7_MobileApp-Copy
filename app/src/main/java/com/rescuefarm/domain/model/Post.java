package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.PostStatus;
import com.rescuefarm.domain.enums.UrgencyLevel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private String id; private String sellerId; private String campaignId;
    private String title; private String content; private List<String> imageUrls;
    private List<String> linkedProductIds; private UrgencyLevel urgencyLevel;
    private PostStatus status; private long viewCount; private Date createdAt;

    public Post() {
        imageUrls = new ArrayList<>(); linkedProductIds = new ArrayList<>();
        urgencyLevel = UrgencyLevel.NORMAL; status = PostStatus.DRAFT;
    }
    public Post(String id, String sellerId) {
        this(); this.id = clean(id); this.sellerId = require(sellerId, "Seller is required");
    }
    public static Post restore(String id, String sellerId, String campaignId, String title,
            String content, List<String> imageUrls, List<String> linkedProductIds,
            UrgencyLevel urgencyLevel, PostStatus status, long viewCount, Date createdAt) {
        Post value = new Post(id, sellerId);
        value.defineContent(campaignId, title, content, imageUrls, linkedProductIds, urgencyLevel);
        value.status = status == null ? PostStatus.DRAFT : status;
        value.viewCount = Math.max(0L, viewCount);
        value.createdAt = copy(createdAt);
        return value;
    }
    public void defineContent(String campaignId, String title, String content,
            List<String> imageUrls, List<String> linkedProductIds, UrgencyLevel urgencyLevel) {
        String cleanTitle = clean(title); String cleanContent = clean(content);
        if (cleanTitle.length() < 3 || cleanTitle.length() > 120) {
            throw new IllegalArgumentException("Post title must contain 3-120 characters");
        }
        if (cleanContent.length() < 10 || cleanContent.length() > 3000) {
            throw new IllegalArgumentException("Post content must contain 10-3000 characters");
        }
        this.campaignId = clean(campaignId); this.title = cleanTitle; this.content = cleanContent;
        this.imageUrls = sanitized(imageUrls, 6); this.linkedProductIds = sanitized(linkedProductIds, 12);
        this.urgencyLevel = urgencyLevel == null ? UrgencyLevel.NORMAL : urgencyLevel;
    }
    public void submitForApproval() {
        if (status != PostStatus.DRAFT && status != PostStatus.REJECTED) {
            throw new IllegalStateException("Only draft or rejected posts can be submitted");
        }
        if (title == null || content == null) throw new IllegalStateException("Post content is required");
        status = PostStatus.PENDING_APPROVAL;
    }
    public void increaseView() { viewCount++; }
    private static List<String> sanitized(List<String> values, int maximum) {
        List<String> result = new ArrayList<>();
        if (values != null) for (String value : values) {
            String clean = clean(value); if (!clean.isEmpty() && !result.contains(clean)) result.add(clean);
            if (result.size() == maximum) break;
        }
        return result;
    }
    private static String require(String value, String message) {
        String clean = clean(value); if (clean.isEmpty()) throw new IllegalArgumentException(message); return clean;
    }
    private static String clean(String value) { return value == null ? "" : value.trim(); }
    private static Date copy(Date value) { return value == null ? null : new Date(value.getTime()); }
    public String getId() { return id; } public String getSellerId() { return sellerId; }
    public String getCampaignId() { return campaignId; } public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getImageUrls() { return new ArrayList<>(imageUrls); }
    public List<String> getLinkedProductIds() { return new ArrayList<>(linkedProductIds); }
    public UrgencyLevel getUrgencyLevel() { return urgencyLevel; }
    public PostStatus getStatus() { return status; } public long getViewCount() { return viewCount; }
    public Date getCreatedAt() { return copy(createdAt); }
}
