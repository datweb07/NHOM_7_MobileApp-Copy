package com.rescuefarm.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import com.rescuefarm.data.local.entity.GuestCartItemEntity;
import com.rescuefarm.data.local.mapper.GuestCartMapper;
import com.rescuefarm.domain.model.Cart;
import com.rescuefarm.domain.model.CartItem;
import java.util.Collections;
import org.junit.Test;

public class GuestCartMapperTest {
    @Test public void persistedEntity_restoresQuantityPriceAndSelection() {
        CartItem item = new CartItem("p::b", "p", "b", "seller", 2.5D, 15000D, false);
        GuestCartItemEntity entity = GuestCartMapper.toEntity("guest-1", item);
        Cart restored = GuestCartMapper.toCart("guest-1", Collections.singletonList(entity));
        assertEquals("guest-1", restored.getOwnerKey());
        assertEquals(2.5D, restored.getItems().get(0).getQuantity(), 0D);
        assertEquals(15000D, restored.getItems().get(0).getUnitPrice(), 0D);
        assertFalse(restored.getItems().get(0).isSelected());
    }
}
