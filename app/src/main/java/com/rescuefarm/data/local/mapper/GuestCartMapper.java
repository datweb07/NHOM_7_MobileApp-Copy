package com.rescuefarm.data.local.mapper;

import com.rescuefarm.data.local.entity.GuestCartItemEntity;
import com.rescuefarm.domain.enums.CartOwnerType;
import com.rescuefarm.domain.model.Cart;
import com.rescuefarm.domain.model.CartItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class GuestCartMapper {
    private GuestCartMapper() { }

    public static Cart toCart(String guestId, List<GuestCartItemEntity> entities) {
        List<CartItem> items = new ArrayList<>(); long updatedAt = 0L;
        if (entities != null) for (GuestCartItemEntity entity : entities) {
            try {
                items.add(new CartItem(entity.id, entity.productId, entity.batchId, entity.sellerId,
                        entity.quantity, entity.unitPrice, entity.selected));
                updatedAt = Math.max(updatedAt, entity.updatedAtEpochMillis);
            } catch (IllegalArgumentException ignored) { }
        }
        return new Cart(guestId, CartOwnerType.GUEST, guestId, items,
                new Date(updatedAt == 0L ? System.currentTimeMillis() : updatedAt));
    }

    public static GuestCartItemEntity toEntity(String guestId, CartItem item) {
        GuestCartItemEntity value = new GuestCartItemEntity(item.getId());
        value.guestId = guestId; value.productId = item.getProductId();
        value.batchId = item.getBatchId(); value.sellerId = item.getSellerId();
        value.quantity = item.getQuantity(); value.unitPrice = item.getUnitPrice();
        value.selected = item.isSelected(); value.updatedAtEpochMillis = System.currentTimeMillis();
        return value;
    }
}
