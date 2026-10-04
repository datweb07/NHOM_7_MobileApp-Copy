package com.rescuefarm.domain;

import static org.junit.Assert.assertEquals;

import com.rescuefarm.domain.model.ProductBatch;

import org.junit.Test;

public class ProductBatchTest {
    private static final double DELTA = 0.000001;

    @Test
    public void reserveCommitAndRelease_preserveInventoryInvariant() {
        ProductBatch batch = new ProductBatch("batch-1", "product-1", 100.0);

        batch.reserveStock(30.0);
        batch.commitReservedStock(20.0);
        batch.releaseReservedStock(10.0);

        assertEquals(80.0, batch.getAvailableQuantity(), DELTA);
        assertEquals(0.0, batch.getReservedQuantity(), DELTA);
        assertEquals(20.0, batch.getSoldQuantity(), DELTA);
        batch.verifyInventoryInvariant();
    }

    @Test(expected = IllegalStateException.class)
    public void reserveMoreThanAvailable_isRejected() {
        ProductBatch batch = new ProductBatch("batch-1", "product-1", 10.0);
        batch.reserveStock(11.0);
    }
}
