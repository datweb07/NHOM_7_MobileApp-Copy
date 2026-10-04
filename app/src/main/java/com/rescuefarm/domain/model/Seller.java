package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.SellerStatus;
import com.rescuefarm.domain.enums.UserRole;

public class Seller extends User {
    private String shopName;
    private String representativeName;
    private String shopDescription;
    private String shopAvatarUrl;
    private String address;
    private SellerStatus sellerStatus;
    private double averageRating;

    public Seller() { super(); }

    public Seller(String id, String email, String fullName, String shopName) {
        super(id, email, fullName, UserRole.SELLER);
        this.shopName = shopName;
        this.sellerStatus = SellerStatus.PENDING_APPROVAL;
    }

    public boolean canSell() { return sellerStatus == SellerStatus.APPROVED; }
    public String getShopName() { return shopName; }
    public String getRepresentativeName() { return representativeName; }
    public String getShopDescription() { return shopDescription; }
    public String getShopAvatarUrl() { return shopAvatarUrl; }
    public String getAddress() { return address; }
    public SellerStatus getSellerStatus() { return sellerStatus; }
    public double getAverageRating() { return averageRating; }
}
