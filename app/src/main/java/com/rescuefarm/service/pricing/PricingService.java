package com.rescuefarm.service.pricing;

import com.rescuefarm.domain.enums.PromotionType;
import com.rescuefarm.domain.model.Promotion;

public class PricingService {

    public double calculateFinalUnitPrice(double rescuePrice, double quantity, Promotion promotion) {
        validatePriceAndQuantity(rescuePrice, quantity);
        if (promotion == null || !promotion.isActive()) { return rescuePrice; }

        double discountedPrice = rescuePrice;
        if (promotion.getType() == PromotionType.PERCENT) {
            discountedPrice = rescuePrice * (1.0 - promotion.getValue() / 100.0);
        } else if (promotion.getType() == PromotionType.FIXED) {
            discountedPrice = rescuePrice - promotion.getValue();
        }

        double volumeDiscountPercentage = promotion.calculateDiscount(quantity);
        discountedPrice *= 1.0 - volumeDiscountPercentage / 100.0;
        return Math.max(0.0, discountedPrice);
    }

    public double calculateLineSubtotal(double finalUnitPrice, double quantity) {
        validatePriceAndQuantity(finalUnitPrice, quantity);
        return finalUnitPrice * quantity;
    }

    private static void validatePriceAndQuantity(double price, double quantity) {
        if (!Double.isFinite(price) || price < 0.0) { throw new IllegalArgumentException("Price cannot be negative"); }
        if (!Double.isFinite(quantity) || quantity <= 0.0) { throw new IllegalArgumentException("Quantity must be positive"); }
    }
}
