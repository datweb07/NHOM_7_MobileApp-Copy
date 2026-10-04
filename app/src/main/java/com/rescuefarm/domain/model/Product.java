package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ProductStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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

    public Product(String id, String sellerId, String categoryId, String name, String description,
            double originalPrice, double rescuePrice, String unit, String origin, String province,
            List<String> imageUrls, ProductStatus status) {
        this();
        this.id = id;
        this.sellerId = sellerId;
        updateDetails(categoryId, name, description, originalPrice, rescuePrice, unit, origin,
                province, imageUrls, status);
    }

    public static Product restore(String id, String sellerId, String categoryId, String name,
            String description, double originalPrice, double rescuePrice, String unit,
            String origin, String province, List<String> imageUrls, double averageRating,
            int reviewCount, ProductStatus status) {
        Product product = new Product(id, sellerId, categoryId, name, description, originalPrice,
                rescuePrice, unit, origin, province, imageUrls, status);
        product.averageRating = Math.max(0D, averageRating);
        product.reviewCount = Math.max(0, reviewCount);
        return product;
    }

    public void updateDetails(String categoryId, String name, String description,
            double originalPrice, double rescuePrice, String unit, String origin, String province,
            List<String> imageUrls, ProductStatus status) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (!Double.isFinite(originalPrice) || !Double.isFinite(rescuePrice)
                || originalPrice <= 0 || rescuePrice <= 0 || rescuePrice > originalPrice) {
            throw new IllegalArgumentException("Product prices are invalid");
        }
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Product unit is required");
        }
        this.categoryId = categoryId.trim();
        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
        this.originalPrice = originalPrice;
        this.rescuePrice = rescuePrice;
        this.unit = unit.trim();
        this.origin = origin == null ? "" : origin.trim();
        this.province = province == null ? "" : province.trim();
        this.imageUrls = imageUrls == null ? new ArrayList<>() : new ArrayList<>(imageUrls);
        this.imageUrls.removeAll(Collections.singleton(null));
        this.status = status == null ? ProductStatus.INACTIVE : status;
    }

    public void hide() { status = ProductStatus.HIDDEN; }

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
