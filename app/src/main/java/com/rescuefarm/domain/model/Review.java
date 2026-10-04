package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ReviewStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review {
    private String id;
    private String customerId;
    private String productId;
    private String orderItemId;
    private int rating;
    private String content;
    private List<String> imageUrls;
    private ReviewStatus status;
    private Date createdAt;

    public Review() {
        imageUrls = new ArrayList<>();
        status = ReviewStatus.PENDING;
    }

    public boolean isValid() { return rating >= 1 && rating <= 5 && orderItemId != null && !orderItemId.trim().isEmpty(); }
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getProductId() { return productId; }
    public String getOrderItemId() { return orderItemId; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public List<String> getImageUrls() { return new ArrayList<>(imageUrls); }
    public ReviewStatus getStatus() { return status; }
    public Date getCreatedAt() { return createdAt; }
}
