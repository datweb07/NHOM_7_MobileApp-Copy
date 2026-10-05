package com.rescuefarm.service.cart;

import com.rescuefarm.domain.model.Cart;
import com.rescuefarm.domain.model.CartItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CartGroupingService {
    public Map<String, List<CartItem>> groupSelectedBySeller(Cart cart) {
        Map<String, List<CartItem>> result = new LinkedHashMap<>();
        if (cart == null) return result;
        for (CartItem item : cart.getItems()) {
            if (!item.isSelected()) continue;
            result.computeIfAbsent(item.getSellerId(), ignored -> new ArrayList<>()).add(item);
        }
        return result;
    }
}
