package com.rescuefarm.data.repository;

import android.content.Context;
import com.rescuefarm.data.local.database.RescueFarmDatabase;
import com.rescuefarm.data.local.preferences.SharedPreferencesGuestSessionRepository;
import com.rescuefarm.data.remote.firebase.FirebaseCartRepository;
import com.rescuefarm.service.network.AndroidNetworkStatusProvider;
import java.util.concurrent.Executors;

public final class CartRepositoryFactory {
    private CartRepositoryFactory() { }
    public static CartRepository create(Context context) {
        AuthRepository auth = AuthRepositoryFactory.create(context);
        if (auth.isAuthenticated() && auth.getCurrentUserId() != null) {
            return new FirebaseCartRepository(auth.getCurrentUserId(),
                    new AndroidNetworkStatusProvider(context));
        }
        String guestId = new SharedPreferencesGuestSessionRepository(context).getOrCreateGuestId();
        return new GuestCartRepository(RescueFarmDatabase.getInstance(context).guestCartDao(),
                guestId, Executors.newSingleThreadExecutor());
    }
}
