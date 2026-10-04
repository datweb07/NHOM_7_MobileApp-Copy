package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ProductStatus;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private String id;
    private String sellerId;
    private String categoryId;
    private String name;
    private String description;
    private double originalPrice;
    private double rescuePrice;
    private String unit;
    private String origin;
    private String province;
    private List<String> imageUrls;
    private double averageRating;
    private int reviewCount;
    private ProductStatus status;

    public Product() { imageUrls = new ArrayList<>(); }

    public double calculateDiscountPercent() {
        if (originalPrice <= 0.0 || rescuePrice >= originalPrice) { return 0.0; }
        return ((originalPrice - rescuePrice) / originalPrice) * 100.0;
    }

    public boolean isAvailable() { return status == ProductStatus.ACTIVE; }
    public String getId() { return id; }
    public String getSellerId() { return sellerId; }
    public String getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getOriginalPrice() { return originalPrice; }
    public double getRescuePrice() { return rescuePrice; }
    public String getUnit() { return unit; }
    public String getOrigin() { return origin; }
    public String getProvince() { return province; }
    public List<String> getImageUrls() { return new ArrayList<>(imageUrls); }
    public double getAverageRating() { return averageRating; }
    public int getReviewCount() { return reviewCount; }
    public ProductStatus getStatus() { return status; }
}
