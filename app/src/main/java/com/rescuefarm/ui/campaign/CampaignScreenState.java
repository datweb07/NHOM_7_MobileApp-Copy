package com.rescuefarm.ui.campaign;

import com.rescuefarm.domain.model.RescueCampaign;

public final class CampaignScreenState {
    public enum Status { IDLE, LOADING, CAMPAIGN, SAVED, LOCATION_UPDATED, ERROR }
    private final Status status; private final RescueCampaign campaign; private final String message;
    private CampaignScreenState(Status status, RescueCampaign campaign, String message) {
        this.status = status; this.campaign = campaign; this.message = message;
    }
    public static CampaignScreenState idle() { return value(Status.IDLE, null); }
    public static CampaignScreenState loading() { return value(Status.LOADING, null); }
    public static CampaignScreenState campaign(RescueCampaign value) {
        return new CampaignScreenState(Status.CAMPAIGN, value, null);
    }
    public static CampaignScreenState saved(RescueCampaign value, String message) {
        return new CampaignScreenState(Status.SAVED, value, message);
    }
    public static CampaignScreenState location(RescueCampaign value, String message) {
        return new CampaignScreenState(Status.LOCATION_UPDATED, value, message);
    }
    public static CampaignScreenState error(String message) { return value(Status.ERROR, message); }
    private static CampaignScreenState value(Status status, String message) {
        return new CampaignScreenState(status, null, message);
    }
    public Status getStatus() { return status; }
    public RescueCampaign getCampaign() { return campaign; }
    public String getMessage() { return message; }
}
