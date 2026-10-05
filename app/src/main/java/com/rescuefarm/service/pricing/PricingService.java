package com.rescuefarm.service.pricing;

import com.rescuefarm.domain.enums.PromotionType;
import com.rescuefarm.domain.model.Promotion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class PricingService {

    public double calculateFinalUnitPrice(double rescuePrice, double quantity, Promotion promotion) {
        return calculatePrice(rescuePrice, rescuePrice, quantity, promotion, new Date()).getFinalUnitPrice();
    }

    public PriceBreakdown calculatePrice(double originalPrice, double rescuePrice, double quantity,
            Promotion promotion, Date now) {
        validatePriceAndQuantity(originalPrice, quantity);
        validatePriceAndQuantity(rescuePrice, quantity);
        if (rescuePrice > originalPrice) throw new IllegalArgumentException("Rescue price cannot exceed original price");
        if (now == null) throw new IllegalArgumentException("Pricing time is required");

        double promotionDiscount = 0D;
        double volumeDiscountPercent = 0D;
        boolean applied = promotion != null && promotion.isValid(now);
        if (applied) {
            if (promotion.getType() == PromotionType.PERCENT) {
                promotionDiscount = rescuePrice * promotion.getValue() / 100D;
            } else if (promotion.getType() == PromotionType.FIXED) {
                promotionDiscount = promotion.getValue();
            }
            promotionDiscount = Math.min(rescuePrice, promotionDiscount);
            volumeDiscountPercent = promotion.calculateDiscount(quantity);
        }
        double afterPromotion = Math.max(0D, rescuePrice - promotionDiscount);
        double finalUnitPrice = roundVnd(afterPromotion * (1D - volumeDiscountPercent / 100D));
        double subtotal = roundVnd(finalUnitPrice * quantity);
        return new PriceBreakdown(roundVnd(originalPrice), roundVnd(rescuePrice),
                roundVnd(promotionDiscount), volumeDiscountPercent, finalUnitPrice,
                quantity, subtotal, applied);
    }

    public double calculateLineSubtotal(double finalUnitPrice, double quantity) {
        validatePriceAndQuantity(finalUnitPrice, quantity);
        return roundVnd(finalUnitPrice * quantity);
    }

    private static void validatePriceAndQuantity(double price, double quantity) {
        if (!Double.isFinite(price) || price < 0.0) { throw new IllegalArgumentException("Price cannot be negative"); }
        if (!Double.isFinite(quantity) || quantity <= 0.0) { throw new IllegalArgumentException("Quantity must be positive"); }
    }

    private static double roundVnd(double value) {
        return BigDecimal.valueOf(Math.max(0D, value)).setScale(0, RoundingMode.HALF_UP).doubleValue();
    }
}
