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

    public void addItem(CartItem item) {
        if (item == null) { throw new IllegalArgumentException("Cart item is required"); }
        items.add(item);
    }

    public void removeItem(String cartItemId) { items.removeIf(item -> item.getId().equals(cartItemId)); }

    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (CartItem item : items) { if (item.isSelected()) { subtotal += item.calculateTotal(); } }
        return subtotal;
    }

    public String getId() { return id; }
    public CartOwnerType getOwnerType() { return ownerType; }
    public String getOwnerKey() { return ownerKey; }
    public List<CartItem> getItems() { return new ArrayList<>(items); }
    public Date getUpdatedAt() { return updatedAt; }
}
