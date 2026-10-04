package com.rescuefarm.service.location;

import android.content.Context;

public final class LocationProviderFactory {
    private LocationProviderFactory() { }

    public static LocationProvider create(Context context) {
        return new AndroidLocationProvider(context);
    }
}
