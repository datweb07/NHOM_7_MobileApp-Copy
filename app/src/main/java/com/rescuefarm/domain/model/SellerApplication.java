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
}
