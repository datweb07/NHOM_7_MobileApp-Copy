package com.rescuefarm.data.repository;

public interface GuestSessionRepository {
    String getOrCreateGuestId();
    boolean hasCompletedOnboarding();
    void markOnboardingCompleted();
}
