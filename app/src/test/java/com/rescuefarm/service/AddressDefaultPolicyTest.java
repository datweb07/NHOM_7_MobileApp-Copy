package com.rescuefarm.service;

import com.rescuefarm.service.address.AddressDefaultPolicy;
import org.junit.Test;
import static org.junit.Assert.*;

public class AddressDefaultPolicyTest {
    @Test public void firstAddress_becomesDefault() {
        assertTrue(AddressDefaultPolicy.shouldMakeDefault(false, null, "address-1"));
    }

    @Test public void explicitlySelectedAddress_becomesDefault() {
        assertTrue(AddressDefaultPolicy.shouldMakeDefault(true, "address-1", "address-2"));
    }

    @Test public void editingCurrentDefault_cannotClearDefaultAccidentally() {
        assertTrue(AddressDefaultPolicy.shouldMakeDefault(false, "address-1", "address-1"));
    }

    @Test public void ordinaryAddress_doesNotReplaceExistingDefault() {
        assertFalse(AddressDefaultPolicy.shouldMakeDefault(false, "address-1", "address-2"));
    }
}
