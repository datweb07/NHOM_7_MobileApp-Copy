package com.rescuefarm.ui.auth;

import com.rescuefarm.domain.model.User;

public final class AuthScreenState {
    public enum Status {
        IDLE,
        LOADING,
        ONBOARDING_REQUIRED,
        LOGIN_REQUIRED,
        AUTHENTICATED,
        GUEST,
        RESET_EMAIL_SENT,
        ERROR
    }

    private final Status status;
    private final User user;
    private final String guestId;
    private final String message;

    private AuthScreenState(Status status, User user, String guestId, String message) {
        this.status = status;
        this.user = user;
        this.guestId = guestId;
        this.message = message;
    }

    public static AuthScreenState idle() { return new AuthScreenState(Status.IDLE, null, null, null); }
    public static AuthScreenState loading() { return new AuthScreenState(Status.LOADING, null, null, null); }
    public static AuthScreenState onboardingRequired() { return new AuthScreenState(Status.ONBOARDING_REQUIRED, null, null, null); }
    public static AuthScreenState loginRequired() { return new AuthScreenState(Status.LOGIN_REQUIRED, null, null, null); }
    public static AuthScreenState authenticated(User user) { return new AuthScreenState(Status.AUTHENTICATED, user, null, null); }
    public static AuthScreenState guest(String guestId) { return new AuthScreenState(Status.GUEST, null, guestId, null); }
    public static AuthScreenState resetEmailSent(String message) { return new AuthScreenState(Status.RESET_EMAIL_SENT, null, null, message); }
    public static AuthScreenState error(String message) { return new AuthScreenState(Status.ERROR, null, null, message); }

    public Status getStatus() { return status; }
    public User getUser() { return user; }
    public String getGuestId() { return guestId; }
    public String getMessage() { return message; }
}
