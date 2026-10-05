package com.rescuefarm.service.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public final class AndroidNetworkStatusProvider implements NetworkStatusProvider {
    private final ConnectivityManager connectivityManager;
    public AndroidNetworkStatusProvider(Context context) {
        connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    @Override public boolean isOnline() {
        if (connectivityManager == null) return false;
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}
