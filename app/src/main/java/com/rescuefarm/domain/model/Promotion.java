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

    public Promotion(String id, String sellerId, String productId, PromotionType type, double value,
            Map<String, Double> quantityDiscountTiers, Date startDate, Date endDate, boolean active) {
        this.id = required(id, "Promotion id is required");
        this.sellerId = required(sellerId, "Seller is required");
        this.productId = required(productId, "Product is required");
        this.type = requireType(type);
        this.value = value;
        this.quantityDiscountTiers = copyAndValidateTiers(quantityDiscountTiers);
        this.startDate = copy(startDate);
        this.endDate = copy(endDate);
        this.active = active;
        validate();
    }

    public static Promotion restore(String id, String sellerId, String productId, PromotionType type,
            double value, Map<String, Double> quantityDiscountTiers, Date startDate, Date endDate,
            boolean active) {
        return new Promotion(id, sellerId, productId, type, value, quantityDiscountTiers,
                startDate, endDate, active);
    }

    public boolean isValid(Date now) {
        return now != null && active && startDate != null && endDate != null
                && !now.before(startDate) && now.before(endDate);
    }

    public double calculateDiscount(double quantity) {
        if (quantity <= 0.0 || quantityDiscountTiers == null) { return 0.0; }
        double bestPercentage = 0.0;
        for (Map.Entry<String, Double> tier : quantityDiscountTiers.entrySet()) {
            double threshold;
            try { threshold = Double.parseDouble(tier.getKey()); }
            catch (NumberFormatException error) { continue; }
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
    public Date getStartDate() { return copy(startDate); }
    public Date getEndDate() { return copy(endDate); }
    public boolean isActive() { return active; }

    private void validate() {
        if (!Double.isFinite(value) || value < 0D
                || (type != PromotionType.VOLUME && value == 0D)
                || (type == PromotionType.VOLUME && value != 0D)) {
            throw new IllegalArgumentException("Promotion value is invalid");
        }
        if (type == PromotionType.PERCENT && value > 100D) {
            throw new IllegalArgumentException("Percent promotion cannot exceed 100");
        }
        if (type == PromotionType.VOLUME && quantityDiscountTiers.isEmpty()) {
            throw new IllegalArgumentException("Volume promotion requires at least one tier");
        }
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            throw new IllegalArgumentException("Promotion period is invalid");
        }
    }

    private static Map<String, Double> copyAndValidateTiers(Map<String, Double> source) {
        Map<String, Double> result = new HashMap<>();
        if (source == null) return result;
        if (source.size() > 10) throw new IllegalArgumentException("A promotion supports at most 10 tiers");
        for (Map.Entry<String, Double> entry : source.entrySet()) {
            double threshold;
            try { threshold = Double.parseDouble(clean(entry.getKey())); }
            catch (NumberFormatException error) { throw new IllegalArgumentException("Tier quantity is invalid"); }
            Double discount = entry.getValue();
            if (!Double.isFinite(threshold) || threshold <= 0D || discount == null
                    || !Double.isFinite(discount) || discount <= 0D || discount > 100D) {
                throw new IllegalArgumentException("Tier quantity/discount is invalid");
            }
            result.put(normalizeNumber(threshold), discount);
        }
        return result;
    }

    private static String normalizeNumber(double value) {
        long whole = (long) value;
        return value == whole ? Long.toString(whole) : Double.toString(value);
    }
    private static PromotionType requireType(PromotionType value) {
        if (value == null) throw new IllegalArgumentException("Promotion type is required");
        return value;
    }
    private static String required(String value, String message) {
        String clean = clean(value);
        if (clean.isEmpty()) throw new IllegalArgumentException(message);
        return clean;
    }
    private static String clean(String value) { return value == null ? "" : value.trim(); }
    private static Date copy(Date value) { return value == null ? null : new Date(value.getTime()); }
}
