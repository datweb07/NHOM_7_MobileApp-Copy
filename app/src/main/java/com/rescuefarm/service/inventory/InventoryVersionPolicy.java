package com.rescuefarm.service.inventory;

public final class InventoryVersionPolicy {
    private InventoryVersionPolicy() { }

    public static void requireExpected(long actualVersion, long expectedVersion) {
        if (actualVersion != expectedVersion) {
            throw new IllegalStateException("STALE_VERSION");
        }
    }
}
