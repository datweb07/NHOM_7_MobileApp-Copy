package com.rescuefarm.domain.model;

public class Category {
    private String id;
    private String name;
    private String imageUrl;
    private boolean active;
    private int displayOrder;

    public Category() { }

    public Category(String id, String name, String imageUrl, boolean active, int displayOrder) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
        this.id = id;
        this.name = name.trim();
        this.imageUrl = imageUrl == null ? "" : imageUrl.trim();
        this.active = active;
        this.displayOrder = Math.max(0, displayOrder);
    }

    public void activate() { active = true; }
    public void deactivate() { active = false; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return active; }
    public int getDisplayOrder() { return displayOrder; }
}
