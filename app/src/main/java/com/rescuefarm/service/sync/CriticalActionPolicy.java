package com.rescuefarm.service.sync;

public final class CriticalActionPolicy {
    public enum Action { CHECKOUT, PAYMENT, APPROVAL, INVENTORY, ORDER_TRANSITION, READ_SYNC }

    public boolean mayQueue(Action action) { return action == Action.READ_SYNC; }

    public void requireOnline(Action action, boolean online) {
        if (!online && !mayQueue(action)) {
            throw new IllegalStateException("OFFLINE_CRITICAL_ACTION_BLOCKED");
        }
    }
}
