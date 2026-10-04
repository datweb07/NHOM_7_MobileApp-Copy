package com.rescuefarm.domain.model;

public class Category {
    private String id;
    private String name;
    private String imageUrl;
    private boolean active;
    private int displayOrder;

    public Category() { }

    public void activate() { active = true; }
    public void deactivate() { active = false; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return active; }
    public int getDisplayOrder() { return displayOrder; }
}
