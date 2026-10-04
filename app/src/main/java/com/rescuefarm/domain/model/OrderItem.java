package com.rescuefarm.domain.model;

public class OrderItem {
    private String id;
    private String orderId;
    private String productId;
    private String batchId;
    private String productNameSnapshot;
    private String productImageSnapshot;
    private String unitSnapshot;
    private double originalPriceSnapshot;
    private double rescuePriceSnapshot;
    private double finalPriceSnapshot;
    private double quantity;
    private double subtotal;

    public OrderItem() { }

    public OrderItem(String productId, String batchId, String productNameSnapshot, String productImageSnapshot,
                     String unitSnapshot, double originalPriceSnapshot, double rescuePriceSnapshot,
                     double finalPriceSnapshot, double quantity) {
        if (quantity <= 0.0 || finalPriceSnapshot < 0.0) { throw new IllegalArgumentException("Order item quantity and final price are invalid"); }
        this.productId = productId;
        this.batchId = batchId;
        this.productNameSnapshot = productNameSnapshot;
        this.productImageSnapshot = productImageSnapshot;
        this.unitSnapshot = unitSnapshot;
        this.originalPriceSnapshot = originalPriceSnapshot;
        this.rescuePriceSnapshot = rescuePriceSnapshot;
        this.finalPriceSnapshot = finalPriceSnapshot;
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    public double calculateSubtotal() { return finalPriceSnapshot * quantity; }
    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }
    public String getBatchId() { return batchId; }
    public String getProductNameSnapshot() { return productNameSnapshot; }
    public String getProductImageSnapshot() { return productImageSnapshot; }
    public String getUnitSnapshot() { return unitSnapshot; }
    public double getOriginalPriceSnapshot() { return originalPriceSnapshot; }
    public double getRescuePriceSnapshot() { return rescuePriceSnapshot; }
    public double getFinalPriceSnapshot() { return finalPriceSnapshot; }
    public double getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
}
