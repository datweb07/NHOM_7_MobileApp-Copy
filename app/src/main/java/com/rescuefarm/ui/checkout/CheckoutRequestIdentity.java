package com.rescuefarm.ui.checkout;

import androidx.lifecycle.SavedStateHandle;
import java.util.UUID;

final class CheckoutRequestIdentity {
    private static final String KEY = "checkout.requestId";
    private CheckoutRequestIdentity() { }

    static String getOrCreate(SavedStateHandle state) {
        String id = state.get(KEY);
        if (id == null || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString();
            state.set(KEY, id);
        }
        return id;
    }
}
