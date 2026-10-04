package com.rescuefarm.data.repository;

import android.content.Context;

import com.rescuefarm.data.remote.firebase.FirebaseConfiguration;
import com.rescuefarm.data.remote.firebase.FirebaseUserRepository;

public final class UserRepositoryFactory {
    private UserRepositoryFactory() { }

    public static UserRepository create(Context context) {
        if (!FirebaseConfiguration.isConfigured(context)) {
            return new UnavailableUserRepository();
        }
        return new FirebaseUserRepository();
    }
}
