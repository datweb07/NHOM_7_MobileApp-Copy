package com.rescuefarm.data.repository;

public interface AuthRepository {
    boolean isAuthenticated();
    String getCurrentUserId();
}
