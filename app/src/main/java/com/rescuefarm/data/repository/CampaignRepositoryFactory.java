package com.rescuefarm.data.repository;

import android.content.Context;
import com.rescuefarm.data.local.database.RescueFarmDatabase;
import com.rescuefarm.data.remote.firebase.FirebaseCampaignRepository;
import com.rescuefarm.data.remote.firebase.FirebaseConfiguration;
import java.util.concurrent.Executors;
import com.rescuefarm.service.network.AndroidNetworkStatusProvider;

public final class CampaignRepositoryFactory {
    private CampaignRepositoryFactory() { }
    public static CampaignRepository create(Context context) {
        RescueFarmDatabase database = RescueFarmDatabase.getInstance(context);
        if (!FirebaseConfiguration.isConfigured(context)) {
            return new OfflineCampaignRepository(database.campaignCacheDao(), database.bannerCacheDao(),
                    Executors.newSingleThreadExecutor());
        }
        return new FirebaseCampaignRepository(database, Executors.newSingleThreadExecutor(),
                new AndroidNetworkStatusProvider(context));
    }
}
