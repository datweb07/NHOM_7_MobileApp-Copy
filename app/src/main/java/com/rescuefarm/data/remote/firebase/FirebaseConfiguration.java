package com.rescuefarm.data.remote.firebase;

import android.content.Context;

public final class FirebaseConfiguration {
    private static final String GOOGLE_APP_ID_RESOURCE = "google_app_id";

    private FirebaseConfiguration() { }

    public static boolean isConfigured(Context context) {
        int resourceId = context.getResources().getIdentifier(
                GOOGLE_APP_ID_RESOURCE,
                "string",
                context.getPackageName()
        );
        return resourceId != 0;
    }
}
