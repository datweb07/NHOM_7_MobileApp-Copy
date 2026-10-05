package com.rescuefarm.service.pricing;

/** Immutable output shared by Product Detail and the future Cart. Amounts are VND. */
public final class PriceBreakdown {
    private final double originalPrice;
    private final double rescuePrice;
    private final double promotionDiscount;
    private final double volumeDiscountPercent;
    private final double finalUnitPrice;
    private final double quantity;
    private final double lineSubtotal;
    private final boolean promotionApplied;

    public PriceBreakdown(double originalPrice, double rescuePrice, double promotionDiscount,
            double volumeDiscountPercent, double finalUnitPrice, double quantity,
            double lineSubtotal, boolean promotionApplied) {
        this.originalPrice = originalPrice;
        this.rescuePrice = rescuePrice;
        this.promotionDiscount = promotionDiscount;
        this.volumeDiscountPercent = volumeDiscountPercent;
        this.finalUnitPrice = finalUnitPrice;
        this.quantity = quantity;
        this.lineSubtotal = lineSubtotal;
        this.promotionApplied = promotionApplied;
    }

    public double getOriginalPrice() { return originalPrice; }
    public double getRescuePrice() { return rescuePrice; }
    public double getPromotionDiscount() { return promotionDiscount; }
    public double getVolumeDiscountPercent() { return volumeDiscountPercent; }
    public double getFinalUnitPrice() { return finalUnitPrice; }
    public double getQuantity() { return quantity; }
    public double getLineSubtotal() { return lineSubtotal; }
    public boolean isPromotionApplied() { return promotionApplied; }
}
