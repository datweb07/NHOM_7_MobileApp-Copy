package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.model.RescueCampaign;

import java.util.List;

public interface CampaignRepository {
    LiveData<List<RescueCampaign>> observeActiveCampaigns();
    void refreshCampaigns();
}
