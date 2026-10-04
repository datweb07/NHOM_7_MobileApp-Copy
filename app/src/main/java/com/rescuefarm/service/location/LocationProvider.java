package com.rescuefarm.service.location;

public interface LocationProvider {
    void getCurrentLocation(LocationCallback callback);

    interface LocationCallback {
        void onLocationAvailable(double latitude, double longitude);
        void onLocationUnavailable(String message);
    }
}
