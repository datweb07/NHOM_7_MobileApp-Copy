package com.rescuefarm.service;

import com.rescuefarm.service.inventory.InventoryVersionPolicy;
import org.junit.Test;

public class InventoryVersionPolicyTest {
    @Test public void matchingVersion_isAccepted() {
        InventoryVersionPolicy.requireExpected(4, 4);
    }

    @Test(expected = IllegalStateException.class)
    public void staleWriter_isRejected() {
        InventoryVersionPolicy.requireExpected(5, 4);
    }
}
