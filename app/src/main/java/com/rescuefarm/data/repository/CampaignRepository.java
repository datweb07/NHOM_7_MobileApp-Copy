package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.model.RescueCampaign;

import java.util.List;

public interface CampaignRepository {
    enum ErrorCode { NOT_CONFIGURED, NOT_FOUND, FORBIDDEN, VALIDATION, NETWORK, CONFLICT, UNKNOWN }
    enum ReservationMutation { RESERVE, COMMIT, RELEASE }

    interface ActionCallback {
        void onSuccess();
        void onError(ErrorCode error, String message);
    }
    interface CampaignCallback {
        void onSuccess(RescueCampaign campaign);
        void onError(ErrorCode error, String message);
    }

    LiveData<List<RescueCampaign>> observeActiveCampaigns();
    LiveData<List<RescueCampaign>> observeSellerCampaigns(String sellerId);
    void refreshActiveCampaigns(ActionCallback callback);
    void refreshSellerCampaigns(String sellerId, ActionCallback callback);
    void getCampaign(String campaignId, CampaignCallback callback);
    void saveCampaign(RescueCampaign campaign, boolean submitForApproval,
            CampaignCallback callback);
    void updateMobileLocation(String campaignId, double latitude, double longitude,
            boolean sharingEnabled, CampaignCallback callback);
    void mutateReservation(String campaignId, String batchId, long expectedInventoryVersion,
            ReservationMutation mutation, double quantity, CampaignCallback callback);
    default void close() { }
}
