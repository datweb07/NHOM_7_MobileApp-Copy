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

    public static Notification restore(String id,String userId,NotificationType type,String title,String body,String referenceId,boolean read,Date createdAt){Notification v=new Notification();v.id=required(id);v.userId=required(userId);v.type=type==null?NotificationType.SYSTEM:type;v.title=required(title);v.body=body==null?"":body.trim();v.referenceId=referenceId==null?"":referenceId.trim();v.isRead=read;v.createdAt=copy(createdAt);return v;}

    public void markAsRead() { isRead = true; }
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public NotificationType getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getReferenceId() { return referenceId; }
    public boolean isRead() { return isRead; }
    public Date getCreatedAt() { return copy(createdAt); }
    private static String required(String v){if(v==null||v.trim().isEmpty())throw new IllegalArgumentException("Notification field is required");return v.trim();}private static Date copy(Date v){return v==null?null:new Date(v.getTime());}
}
