package com.rescuefarm.domain;

import com.rescuefarm.domain.model.ProductBatch;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProductBatchBoundaryTest {
    @Test public void expiryExactlyNow_isExpiredAndNotSellable() {
        Date now = new Date(2_000_000L);
        ProductBatch batch = new ProductBatch("b", "p", 10);
        batch.updateDetails(new Date(1_000_000L), now, 10, new Date(1_999_999L));
        assertTrue(batch.isExpired(now));
        assertFalse(batch.isSellable(now));
    }

    @Test(expected = IllegalArgumentException.class)
    public void harvestAfterExpiry_isRejected() {
        ProductBatch batch = new ProductBatch("b", "p", 10);
        batch.updateDetails(new Date(3_000L), new Date(2_000L), 10, new Date(1_000L));
    }

    @Test(expected = IllegalStateException.class)
    public void initialQuantityCannotChangeAfterReservation() {
        ProductBatch batch = new ProductBatch("b", "p", 10);
        batch.updateDetails(new Date(1_000L), new Date(4_000L), 10, new Date(2_000L));
        batch.reserveStock(2, new Date(2_000L));
        batch.updateDetails(new Date(1_000L), new Date(5_000L), 12, new Date(2_000L));
    }
}
