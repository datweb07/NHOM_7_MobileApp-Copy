package com.rescuefarm.domain;

import com.rescuefarm.domain.enums.AddressType;
import com.rescuefarm.domain.model.Address;
import org.junit.Test;
import static org.junit.Assert.*;

public class AddressTest {
    @Test public void defaultFlag_canBeChangedExplicitly() {
        Address address = address(false);
        address.setDefault();
        assertTrue(address.isDefault());
        address.clearDefault();
        assertFalse(address.isDefault());
    }

    @Test public void formattedAddress_containsAdministrativeLevels() {
        assertEquals("12 Lê Lợi, Bến Nghé, Quận 1, TP.HCM", address(false).getFormattedAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCoordinates_areRejected() {
        new Address("a", "u", "An", "0901234567", "TP.HCM", "Quận 1", "Bến Nghé",
                "12 Lê Lợi", 91, 106, AddressType.HOME, false);
    }

    private Address address(boolean isDefault) {
        return new Address("a", "u", "An", "0901234567", "TP.HCM", "Quận 1", "Bến Nghé",
                "12 Lê Lợi", 10.77, 106.70, AddressType.HOME, isDefault);
    }
}
