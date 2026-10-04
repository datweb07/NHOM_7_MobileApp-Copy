package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.UserRole;
import com.rescuefarm.domain.enums.UserStatus;

import java.util.Date;

public abstract class User {
    private String id;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private UserRole role;
    private UserStatus status;
    private double latitude;
    private double longitude;
    private Date createdAt;
    private Date updatedAt;

    protected User() {
        status = UserStatus.ACTIVE;
    }

    protected User(String id, String email, String fullName, UserRole role) {
        this();
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public void updateProfile(
            String fullName,
            String phone,
            String avatarUrl,
            double latitude,
            double longitude,
            Date updatedAt
    ) {
        if (fullName == null || fullName.trim().length() < 2) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        this.fullName = fullName.trim();
        this.phone = phone == null ? "" : phone.trim();
        this.avatarUrl = avatarUrl == null ? "" : avatarUrl.trim();
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = updatedAt == null ? new Date() : new Date(updatedAt.getTime());
    }

    public boolean isActive() { return status == UserStatus.ACTIVE; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAvatarUrl() { return avatarUrl; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}
