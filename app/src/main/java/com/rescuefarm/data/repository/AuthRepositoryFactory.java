package com.rescuefarm.data.repository;

import android.content.Context;

import com.rescuefarm.data.remote.firebase.FirebaseAuthRepository;
import com.rescuefarm.data.remote.firebase.FirebaseConfiguration;

public final class AuthRepositoryFactory {
    private AuthRepositoryFactory() { }

    public static AuthRepository create(Context context) {
        if (!FirebaseConfiguration.isConfigured(context)) {
            return new UnavailableAuthRepository();
        }
        return new FirebaseAuthRepository();
    }
}
