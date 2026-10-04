package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.enums.UserRole;
import com.rescuefarm.domain.model.User;

public interface UserRepository {
    enum ProfileError {
        NOT_FOUND,
        DISABLED,
        NETWORK,
        UNKNOWN
    }

    interface UserCallback {
        void onSuccess(User user);
        void onError(ProfileError error, String message);
    }

    LiveData<User> observeUser(String userId);
    void getUser(String userId, UserCallback callback);
    void createRegistrationProfile(
            String userId,
            String email,
            String fullName,
            String phone,
            String avatarUrl,
            UserRole role,
            UserCallback callback
    );
}
