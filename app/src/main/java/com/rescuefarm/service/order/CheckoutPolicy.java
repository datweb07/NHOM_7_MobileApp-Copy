package com.rescuefarm.service.order;

import com.rescuefarm.domain.model.CartItem;
import java.util.List;
import java.util.Locale;

public final class CheckoutPolicy {
    private CheckoutPolicy() { }
    public static String requireSingleSeller(List<CartItem> items) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Checkout requires selected items");
        if (items.size() > 20) throw new IllegalArgumentException("Checkout supports at most 20 batches");
        String seller = items.get(0).getSellerId();
        for (CartItem item : items) if (!seller.equals(item.getSellerId())) {
            throw new IllegalArgumentException("MULTI_SELLER");
        }
        return seller;
    }
    public static String orderCode(String requestId) {
        String compact = requestId == null ? "" : requestId.replace("-", "").toUpperCase(Locale.US);
        if (compact.length() < 12) throw new IllegalArgumentException("Request id is invalid");
        return "RF-" + compact.substring(0, 12);
    }
}
