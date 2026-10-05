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

    public static Banner restore(String id, String title, String imageUrl, String campaignId,
            int displayOrder, Date startDate, Date endDate, boolean active) {
        if (id == null || id.trim().isEmpty() || title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Banner id and title are required");
        }
        if (startDate == null || endDate == null || !endDate.after(startDate)) {
            throw new IllegalArgumentException("Banner end date must be after start date");
        }
        Banner banner = new Banner(); banner.id = id.trim(); banner.title = title.trim();
        banner.imageUrl = imageUrl == null ? "" : imageUrl.trim();
        banner.campaignId = campaignId == null ? "" : campaignId.trim();
        banner.displayOrder = displayOrder; banner.startDate = new Date(startDate.getTime());
        banner.endDate = new Date(endDate.getTime()); banner.active = active; return banner;
    }

    public void publish() { active = true; }
    public void unpublish() { active = false; }
    public boolean isVisibleAt(Date now) {
        return active && now != null && !now.before(startDate) && now.before(endDate);
    }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public String getCampaignId() { return campaignId; }
    public int getDisplayOrder() { return displayOrder; }
    public Date getStartDate() { return startDate == null ? null : new Date(startDate.getTime()); }
    public Date getEndDate() { return endDate == null ? null : new Date(endDate.getTime()); }
    public boolean isActive() { return active; }
}
