package com.rescuefarm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.rescuefarm.domain.enums.PromotionType;
import com.rescuefarm.domain.model.Promotion;
import com.rescuefarm.service.pricing.PriceBreakdown;
import com.rescuefarm.service.pricing.PricingService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class PricingServiceTest {
    private final PricingService service = new PricingService();

    @Test public void noPromotion_keepsRescuePriceAsFinalUnitPrice() {
        double finalUnitPrice = service.calculateFinalUnitPrice(30000D, 10D, null);
        assertEquals(30000D, finalUnitPrice, 0D);
        assertEquals(300000D, service.calculateLineSubtotal(finalUnitPrice, 10D), 0D);
    }

    @Test public void percentThenVolume_usesRequiredOrder() {
        Promotion promotion = promotion(PromotionType.PERCENT, 10D, tiers("5", 5D), 0, 2000, true);
        PriceBreakdown result = service.calculatePrice(120000D, 100000D, 5D,
                promotion, new Date(1000));
        assertEquals(10000D, result.getPromotionDiscount(), 0D);
        assertEquals(5D, result.getVolumeDiscountPercent(), 0D);
        assertEquals(85500D, result.getFinalUnitPrice(), 0D);
        assertEquals(427500D, result.getLineSubtotal(), 0D);
    }

    @Test public void tierBoundary_appliesOnlyAtThreshold() {
        Promotion promotion = promotion(PromotionType.VOLUME, 0D, tiers("5", 3D), 0, 2000, true);
        assertEquals(0D, service.calculatePrice(100D, 100D, 4.999D,
                promotion, new Date(1000)).getVolumeDiscountPercent(), 0D);
        assertEquals(3D, service.calculatePrice(100D, 100D, 5D,
                promotion, new Date(1000)).getVolumeDiscountPercent(), 0D);
    }

    @Test public void expiredPromotion_isIgnoredAtExclusiveEnd() {
        Promotion promotion = promotion(PromotionType.PERCENT, 50D, new HashMap<>(), 0, 1000, true);
        PriceBreakdown result = service.calculatePrice(100D, 80D, 1D, promotion, new Date(1000));
        assertFalse(result.isPromotionApplied());
        assertEquals(80D, result.getFinalUnitPrice(), 0D);
    }

    @Test public void fixedDiscount_neverCreatesNegativePrice() {
        Promotion promotion = promotion(PromotionType.FIXED, 500D, new HashMap<>(), 0, 2000, true);
        assertEquals(0D, service.calculatePrice(100D, 80D, 1D,
                promotion, new Date(1000)).getFinalUnitPrice(), 0D);
    }

    @Test public void vndRounding_isHalfUp() {
        Promotion promotion = promotion(PromotionType.PERCENT, 50D, new HashMap<>(), 0, 2000, true);
        assertEquals(51D, service.calculatePrice(101D, 101D, 1D,
                promotion, new Date(1000)).getFinalUnitPrice(), 0D);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidTier_isRejected() {
        promotion(PromotionType.VOLUME, 0D, tiers("0", 5D), 0, 2000, true);
    }

    private static Promotion promotion(PromotionType type, double value, Map<String, Double> tiers,
            long start, long end, boolean active) {
        return new Promotion("product-1", "seller-1", "product-1", type, value, tiers,
                new Date(start), new Date(end), active);
    }

    private static Map<String, Double> tiers(String quantity, double discount) {
        Map<String, Double> result = new HashMap<>(); result.put(quantity, discount); return result;
    }
}
