package com.rescuefarm.ui.product;

import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProductValidatorTest {
    @Test public void validPrices_pass() {
        assertNull(ProductValidator.validateProduct("vegetable", "Cà chua", "20000", "12000", "kg"));
    }

    @Test public void invalidDateFormat_fails() {
        assertNotNull(ProductValidator.validateBatch("05/10/2026", "2026-10-20", "10", new Date(0)));
    }
}
