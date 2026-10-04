package com.rescuefarm.domain;

import com.rescuefarm.domain.enums.SellerStatus;
import com.rescuefarm.domain.model.Seller;
import org.junit.Test;
import static org.junit.Assert.*;

public class SellerProfileTest {
    @Test public void pendingSeller_cannotSell() {
        Seller seller = new Seller("s", "seller@example.com", "Seller", "Shop",
                SellerStatus.PENDING_APPROVAL);
        assertFalse(seller.canSell());
    }

    @Test public void approvedSeller_canSell() {
        Seller seller = new Seller("s", "seller@example.com", "Seller", "Shop",
                SellerStatus.APPROVED);
        assertTrue(seller.canSell());
    }
}
