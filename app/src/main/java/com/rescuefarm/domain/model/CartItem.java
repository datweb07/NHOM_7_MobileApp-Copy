package com.rescuefarm.domain.model;

public class CartItem {
    private String id;
    private String productId;
    private String batchId;
    private String sellerId;
    private double quantity;
    private double unitPrice;
    private boolean selected;

    public CartItem() { }

    public CartItem(String id, String productId, String batchId, String sellerId, double quantity, double unitPrice) {
        if (quantity <= 0.0 || unitPrice < 0.0) { throw new IllegalArgumentException("Cart quantity and price are invalid"); }
        this.id = id;
        this.productId = productId;
        this.batchId = batchId;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.selected = true;
    }

    public double calculateTotal() { return quantity * unitPrice; }
    public String getId() { return id; }
    public String getProductId() { return productId; }
    public String getBatchId() { return batchId; }
    public String getSellerId() { return sellerId; }
    public double getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public boolean isSelected() { return selected; }
}
