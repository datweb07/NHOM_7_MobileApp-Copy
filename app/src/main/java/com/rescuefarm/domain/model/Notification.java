package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.NotificationType;

import java.util.Date;

public class Notification {
    private String id;
    private String userId;
    private NotificationType type;
    private String title;
    private String body;
    private String referenceId;
    private boolean isRead;
    private Date createdAt;

    public Notification() { }

    public void markAsRead() { isRead = true; }
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public NotificationType getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getReferenceId() { return referenceId; }
    public boolean isRead() { return isRead; }
    public Date getCreatedAt() { return createdAt; }
}
