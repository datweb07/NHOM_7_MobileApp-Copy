package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.PromotionType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Promotion {
    private String id;
    private String sellerId;
    private String productId;
    private PromotionType type;
    private double value;
    private Map<String, Double> quantityDiscountTiers;
    private Date startDate;
    private Date endDate;
    private boolean active;

    public Promotion() { quantityDiscountTiers = new HashMap<>(); }

    public boolean isValid(Date now) {
        return active && startDate != null && endDate != null && !now.before(startDate) && !now.after(endDate);
    }

    public double calculateDiscount(double quantity) {
        if (quantity <= 0.0 || quantityDiscountTiers == null) { return 0.0; }
        double bestPercentage = 0.0;
        for (Map.Entry<String, Double> tier : quantityDiscountTiers.entrySet()) {
            double threshold = Double.parseDouble(tier.getKey());
            if (quantity >= threshold) { bestPercentage = Math.max(bestPercentage, tier.getValue()); }
        }
        return bestPercentage;
    }

    public String getId() { return id; }
    public String getSellerId() { return sellerId; }
    public String getProductId() { return productId; }
    public PromotionType getType() { return type; }
    public double getValue() { return value; }
    public Map<String, Double> getQuantityDiscountTiers() { return new HashMap<>(quantityDiscountTiers); }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public boolean isActive() { return active; }
}
