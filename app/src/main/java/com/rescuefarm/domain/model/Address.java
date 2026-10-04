package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.AddressType;

public class Address {
    private String id;
    private String customerId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String district;
    private String ward;
    private String street;
    private double latitude;
    private double longitude;
    private AddressType type;
    private boolean isDefault;

    public Address() { }

    public Address(
            String id,
            String customerId,
            String receiverName,
            String receiverPhone,
            String province,
            String district,
            String ward,
            String street,
            double latitude,
            double longitude,
            AddressType type,
            boolean isDefault
    ) {
        this.id = id;
        this.customerId = customerId;
        updateDetails(receiverName, receiverPhone, province, district, ward, street,
                latitude, longitude, type);
        this.isDefault = isDefault;
    }

    public void updateDetails(
            String receiverName,
            String receiverPhone,
            String province,
            String district,
            String ward,
            String street,
            double latitude,
            double longitude,
            AddressType type
    ) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        this.receiverName = clean(receiverName);
        this.receiverPhone = clean(receiverPhone);
        this.province = clean(province);
        this.district = clean(district);
        this.ward = clean(ward);
        this.street = clean(street);
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type == null ? AddressType.HOME : type;
    }

    public String getFormattedAddress() {
        return street + ", " + ward + ", " + district + ", " + province;
    }

    private String clean(String value) { return value == null ? "" : value.trim(); }

    public void setDefault() { isDefault = true; }
    public void clearDefault() { isDefault = false; }
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getProvince() { return province; }
    public String getDistrict() { return district; }
    public String getWard() { return ward; }
    public String getStreet() { return street; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public AddressType getType() { return type; }
    public boolean isDefault() { return isDefault; }
}
