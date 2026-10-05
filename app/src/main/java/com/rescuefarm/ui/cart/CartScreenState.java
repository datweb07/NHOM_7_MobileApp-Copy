package com.rescuefarm.ui.cart;

public final class CartScreenState {
    public enum Status { IDLE, LOADING, LOCAL_READY, SYNCED, OFFLINE, SAVED, REVALIDATED, ERROR }
    private final Status status; private final String message;
    private CartScreenState(Status status, String message) { this.status = status; this.message = message; }
    public static CartScreenState idle() { return new CartScreenState(Status.IDLE, ""); }
    public static CartScreenState of(Status status, String message) { return new CartScreenState(status, message); }
    public Status getStatus() { return status; }
    public String getMessage() { return message; }
}
