package com.rescuefarm.data.repository;

import android.content.Context;
import com.rescuefarm.data.remote.firebase.FirebaseAdminRepository;
import com.rescuefarm.service.network.AndroidNetworkStatusProvider;

public final class AdminRepositoryFactory {
    private AdminRepositoryFactory() { }
    public static AdminRepository create(Context context) {
        return new FirebaseAdminRepository(new AndroidNetworkStatusProvider(
                context.getApplicationContext()));
    }
}
