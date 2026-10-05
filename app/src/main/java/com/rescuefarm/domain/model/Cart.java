package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.CartOwnerType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cart {
    private String id;
    private CartOwnerType ownerType;
    private String ownerKey;
    private List<CartItem> items;
    private Date updatedAt;

    public Cart() { items = new ArrayList<>(); }

    public Cart(String id, CartOwnerType ownerType, String ownerKey, List<CartItem> items,
            Date updatedAt) {
        this.id = required(id, "Cart id");
        if (ownerType == null) throw new IllegalArgumentException("Cart owner type is required");
        this.ownerType = ownerType;
        this.ownerKey = required(ownerKey, "Cart owner key");
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.updatedAt = updatedAt == null ? new Date() : new Date(updatedAt.getTime());
    }

    public static Cart empty(CartOwnerType ownerType, String ownerKey) {
        return new Cart(ownerKey, ownerType, ownerKey, new ArrayList<>(), new Date());
    }

    public void addItem(CartItem item) {
        if (item == null) { throw new IllegalArgumentException("Cart item is required"); }
        for (int index = 0; index < items.size(); index++) {
            CartItem current = items.get(index);
            if (current.getId().equals(item.getId())) {
                items.set(index, item); updatedAt = new Date(); return;
            }
        }
        items.add(item); updatedAt = new Date();
    }

    public void removeItem(String cartItemId) {
        items.removeIf(item -> item.getId().equals(cartItemId)); updatedAt = new Date();
    }

    public void updateQuantity(String cartItemId, double quantity) {
        for (int index = 0; index < items.size(); index++) {
            CartItem item = items.get(index);
            if (item.getId().equals(cartItemId)) {
                items.set(index, item.withQuantity(quantity)); updatedAt = new Date(); return;
            }
        }
        throw new IllegalArgumentException("Cart item was not found");
    }

    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (CartItem item : items) { if (item.isSelected()) { subtotal += item.calculateTotal(); } }
        return subtotal;
    }

    public String getId() { return id; }
    public CartOwnerType getOwnerType() { return ownerType; }
    public String getOwnerKey() { return ownerKey; }
    public List<CartItem> getItems() { return new ArrayList<>(items); }
    public Date getUpdatedAt() { return updatedAt == null ? null : new Date(updatedAt.getTime()); }
    private static String required(String value, String field) {
        String clean = value == null ? "" : value.trim();
        if (clean.isEmpty()) throw new IllegalArgumentException(field + " is required");
        return clean;
    }
}
