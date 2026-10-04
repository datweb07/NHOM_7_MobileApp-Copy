package com.rescuefarm.domain.model;

import java.util.Date;

public class Banner {
    private String id;
    private String title;
    private String imageUrl;
    private String campaignId;
    private int displayOrder;
    private Date startDate;
    private Date endDate;
    private boolean active;

    public Banner() { }

    public void publish() { active = true; }
    public void unpublish() { active = false; }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public String getCampaignId() { return campaignId; }
    public int getDisplayOrder() { return displayOrder; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public boolean isActive() { return active; }
}
