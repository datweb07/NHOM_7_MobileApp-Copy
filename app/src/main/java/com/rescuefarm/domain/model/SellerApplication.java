package com.rescuefarm.domain.model;

import com.rescuefarm.domain.enums.ApplicationStatus;

import java.util.Date;

public class SellerApplication {
    private String id;
    private String sellerId;
    private String representativeName;
    private String shopName;
    private String address;
    private String proofImageUrl;
    private ApplicationStatus status;
    private String rejectionReason;
    private Date submittedAt;

    public SellerApplication() { status = ApplicationStatus.DRAFT; }

    public SellerApplication(
            String id,
            String sellerId,
            String representativeName,
            String shopName,
            String address,
            String proofImageUrl
    ) {
        this();
        this.id = id;
        this.sellerId = sellerId;
        this.representativeName = clean(representativeName);
        this.shopName = clean(shopName);
        this.address = clean(address);
        this.proofImageUrl = clean(proofImageUrl);
    }

    public static SellerApplication restore(
            String id,
            String sellerId,
            String representativeName,
            String shopName,
            String address,
            String proofImageUrl,
            ApplicationStatus status,
            String rejectionReason,
            Date submittedAt
    ) {
        SellerApplication application = new SellerApplication(
                id, sellerId, representativeName, shopName, address, proofImageUrl
        );
        application.status = status == null ? ApplicationStatus.DRAFT : status;
        application.rejectionReason = clean(rejectionReason);
        application.submittedAt = submittedAt;
        return application;
    }

    public void submit(Date submissionTime) {
        if (status != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("Only a draft seller application can be submitted");
        }
        status = ApplicationStatus.PENDING;
        submittedAt = submissionTime;
    }

    public String getId() { return id; }
    public String getSellerId() { return sellerId; }
    public String getRepresentativeName() { return representativeName; }
    public String getShopName() { return shopName; }
    public String getAddress() { return address; }
    public String getProofImageUrl() { return proofImageUrl; }
    public ApplicationStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public Date getSubmittedAt() { return submittedAt; }

    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
