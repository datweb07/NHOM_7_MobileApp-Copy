package com.rescuefarm.data.repository;

import android.content.Context;
import com.rescuefarm.data.local.database.RescueFarmDatabase;
import com.rescuefarm.data.remote.firebase.FirebaseConfiguration;
import com.rescuefarm.data.remote.firebase.FirebaseProductRepository;
import java.util.concurrent.Executors;

public final class ProductRepositoryFactory {
    private ProductRepositoryFactory() { }

    public static ProductRepository create(Context context) {
        RescueFarmDatabase database = RescueFarmDatabase.getInstance(context);
        if (!FirebaseConfiguration.isConfigured(context)) {
            return new OfflineProductRepository(
                    database.catalogCacheDao(), Executors.newSingleThreadExecutor());
        }
        return new FirebaseProductRepository(database, Executors.newSingleThreadExecutor());
    }
}
