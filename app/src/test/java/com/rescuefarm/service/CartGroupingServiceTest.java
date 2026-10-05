package com.rescuefarm.service;

import static org.junit.Assert.assertEquals;
import com.rescuefarm.domain.enums.CartOwnerType;
import com.rescuefarm.domain.model.Cart;
import com.rescuefarm.domain.model.CartItem;
import com.rescuefarm.service.cart.CartGroupingService;
import java.util.Arrays;
import java.util.Date;
import org.junit.Test;

public class CartGroupingServiceTest {
    @Test public void selectedItems_areSplitBySeller() {
        Cart cart = new Cart("guest-1", CartOwnerType.GUEST, "guest-1", Arrays.asList(
                new CartItem("a", "p1", "b1", "s1", 2D, 10D, true),
                new CartItem("b", "p2", "b2", "s2", 1D, 20D, true),
                new CartItem("c", "p3", "b3", "s1", 1D, 30D, false)), new Date());
        assertEquals(2, new CartGroupingService().groupSelectedBySeller(cart).size());
        assertEquals(1, new CartGroupingService().groupSelectedBySeller(cart).get("s1").size());
        assertEquals(40D, cart.calculateSubtotal(), 0D);
    }
}
