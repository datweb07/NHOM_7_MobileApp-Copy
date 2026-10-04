package com.rescuefarm.domain;

import com.rescuefarm.domain.enums.ProductStatus;
import com.rescuefarm.domain.model.Product;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProductBoundaryTest {
    @Test public void rescuePriceEqualOriginal_isValidWithZeroDiscount() {
        Product product = product(10_000, 10_000);
        assertEquals(0D, product.calculateDiscountPercent(), 0.000001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescuePriceAboveOriginal_isRejected() { product(10_000, 10_001); }

    @Test(expected = IllegalArgumentException.class)
    public void nonFinitePrice_isRejected() { product(Double.NaN, 1); }

    private Product product(double original, double rescue) {
        return new Product("p", "s", "c", "Cà chua", "", original, rescue, "kg",
                "Đà Lạt", "Lâm Đồng", Collections.emptyList(), ProductStatus.ACTIVE);
    }
}
