package com.rescuefarm.service;

import static org.junit.Assert.assertEquals;

import com.rescuefarm.service.pricing.PricingService;

import org.junit.Test;

public class PricingServiceTest {
    private static final double DELTA = 0.000001;

    @Test
    public void noPromotion_keepsRescuePriceAsFinalUnitPrice() {
        PricingService service = new PricingService();

        double finalUnitPrice = service.calculateFinalUnitPrice(30000.0, 10.0, null);

        assertEquals(30000.0, finalUnitPrice, DELTA);
        assertEquals(300000.0, service.calculateLineSubtotal(finalUnitPrice, 10.0), DELTA);
    }
}
