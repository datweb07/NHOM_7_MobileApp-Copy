package com.rescuefarm.domain.model;

import java.util.Date;

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
        this(id, productId, batchId, sellerId, quantity, unitPrice, true);
    }

    public CartItem(String id, String productId, String batchId, String sellerId, double quantity,
            double unitPrice, boolean selected) {
        requirePositive(quantity, "Cart quantity");
        if (!Double.isFinite(unitPrice) || unitPrice < 0.0) {
            throw new IllegalArgumentException("Cart price is invalid");
        }
        this.id = required(id, "Cart item id");
        this.productId = required(productId, "Product id");
        this.batchId = required(batchId, "Batch id");
        this.sellerId = required(sellerId, "Seller id");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.selected = selected;
    }

    public double calculateTotal() { return quantity * unitPrice; }
    public boolean validateStock(ProductBatch batch, Date now) {
        return batch != null && productId.equals(batch.getProductId())
                && batchId.equals(batch.getId()) && batch.isSellable(now)
                && batch.hasAvailableStock(quantity);
    }
    public boolean validateStock(double availableQuantity) {
        return Double.isFinite(availableQuantity) && availableQuantity >= quantity;
    }
    public CartItem withQuantity(double value) {
        return new CartItem(id, productId, batchId, sellerId, value, unitPrice, selected);
    }
    public CartItem withSelection(boolean value) {
        return new CartItem(id, productId, batchId, sellerId, quantity, unitPrice, value);
    }
    public CartItem withQuote(double price) {
        return new CartItem(id, productId, batchId, sellerId, quantity, price, selected);
    }
    public static String itemId(String productId, String batchId) {
        return required(productId, "Product id") + "::" + required(batchId, "Batch id");
    }
    public String getId() { return id; }
    public String getProductId() { return productId; }
    public String getBatchId() { return batchId; }
    public String getSellerId() { return sellerId; }
    public double getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public boolean isSelected() { return selected; }

    private static void requirePositive(double value, String field) {
        if (!Double.isFinite(value) || value <= 0D) throw new IllegalArgumentException(field + " must be positive");
    }
    private static String required(String value, String field) {
        String clean = value == null ? "" : value.trim();
        if (clean.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return clean;
    }
}
