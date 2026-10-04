package com.rescuefarm.ui.profile;

import com.rescuefarm.domain.model.Address;
import com.rescuefarm.domain.model.SellerApplication;
import com.rescuefarm.domain.model.User;
import com.rescuefarm.service.location.LocationProvider;
import java.util.Collections;
import java.util.List;

public final class ProfileScreenState {
    public enum Status { IDLE, LOADING, PROFILE, ADDRESSES, APPLICATION, LOCATION, SAVED, ERROR }
    private final Status status;
    private final User user;
    private final List<Address> addresses;
    private final SellerApplication application;
    private final double latitude;
    private final double longitude;
    private final LocationProvider.GeocodedAddress geocodedAddress;
    private final String message;

    private ProfileScreenState(Status status, User user, List<Address> addresses,
            SellerApplication application, double latitude, double longitude,
            LocationProvider.GeocodedAddress geocodedAddress, String message) {
        this.status = status; this.user = user;
        this.addresses = addresses == null ? Collections.emptyList() : addresses;
        this.application = application; this.latitude = latitude; this.longitude = longitude;
        this.geocodedAddress = geocodedAddress; this.message = message;
    }

    public static ProfileScreenState idle() { return value(Status.IDLE, null); }
    public static ProfileScreenState loading() { return value(Status.LOADING, null); }
    public static ProfileScreenState profile(User user) {
        return new ProfileScreenState(Status.PROFILE, user, null, null, 0, 0, null, null);
    }
    public static ProfileScreenState addresses(List<Address> addresses) {
        return new ProfileScreenState(Status.ADDRESSES, null, addresses, null, 0, 0, null, null);
    }
    public static ProfileScreenState application(SellerApplication application) {
        return new ProfileScreenState(Status.APPLICATION, null, null, application, 0, 0, null, null);
    }
    public static ProfileScreenState location(double latitude, double longitude,
            LocationProvider.GeocodedAddress address) {
        return location(latitude, longitude, address, null);
    }
    public static ProfileScreenState location(double latitude, double longitude,
            LocationProvider.GeocodedAddress address, String message) {
        return new ProfileScreenState(Status.LOCATION, null, null, null, latitude, longitude, address, message);
    }
    public static ProfileScreenState saved(String message) { return value(Status.SAVED, message); }
    public static ProfileScreenState error(String message) { return value(Status.ERROR, message); }
    private static ProfileScreenState value(Status status, String message) {
        return new ProfileScreenState(status, null, null, null, 0, 0, null, message);
    }

    public Status getStatus() { return status; }
    public User getUser() { return user; }
    public List<Address> getAddresses() { return addresses; }
    public SellerApplication getApplication() { return application; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public LocationProvider.GeocodedAddress getGeocodedAddress() { return geocodedAddress; }
    public String getMessage() { return message; }
}
