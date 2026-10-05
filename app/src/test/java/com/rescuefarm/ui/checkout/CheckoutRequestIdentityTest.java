package com.rescuefarm.ui.checkout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import androidx.lifecycle.SavedStateHandle;
import java.util.Collections;
import org.junit.Test;

public class CheckoutRequestIdentityTest {
    @Test public void reusesCheckoutIdAfterStateRecreation() {
        SavedStateHandle first = new SavedStateHandle();
        String original = CheckoutRequestIdentity.getOrCreate(first);
        SavedStateHandle recreated = new SavedStateHandle(
                Collections.<String, Object>singletonMap("checkout.requestId", original));

        assertEquals(original, CheckoutRequestIdentity.getOrCreate(recreated));
    }

    @Test public void createsDifferentIdForNewCheckout() {
        assertFalse(CheckoutRequestIdentity.getOrCreate(new SavedStateHandle()).equals(
                CheckoutRequestIdentity.getOrCreate(new SavedStateHandle())));
    }
}
