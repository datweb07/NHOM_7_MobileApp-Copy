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
