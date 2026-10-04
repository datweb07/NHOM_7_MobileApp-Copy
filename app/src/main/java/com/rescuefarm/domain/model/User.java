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
