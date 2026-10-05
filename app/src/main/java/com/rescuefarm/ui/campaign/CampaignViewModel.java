package com.rescuefarm.ui.campaign;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.rescuefarm.data.repository.AuthRepository;
import com.rescuefarm.data.repository.CampaignRepository;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.model.RescueCampaign;
import com.rescuefarm.service.location.LocationProvider;
import java.util.List;
import java.util.Map;

public class CampaignViewModel extends ViewModel {
    private final CampaignRepository repository; private final AuthRepository authRepository;
    private final LocationProvider locationProvider;
    private final MutableLiveData<CampaignScreenState> state =
            new MutableLiveData<>(CampaignScreenState.idle());
    public CampaignViewModel(CampaignRepository repository, AuthRepository authRepository,
            LocationProvider locationProvider) {
        this.repository = repository; this.authRepository = authRepository;
        this.locationProvider = locationProvider;
    }
    public LiveData<CampaignScreenState> getState() { return state; }
    public LiveData<List<RescueCampaign>> getActiveCampaigns() { return repository.observeActiveCampaigns(); }
    public LiveData<List<RescueCampaign>> getSellerCampaigns() {
        return repository.observeSellerCampaigns(currentUserId());
    }
    public void refreshActive() { repository.refreshActiveCampaigns(action(null)); }
    public void refreshSeller() {
        String id = requireSeller(); if (id != null) repository.refreshSellerCampaigns(id, action(null));
    }
    public void load(String campaignId) {
        state.setValue(CampaignScreenState.loading());
        repository.getCampaign(campaignId, callback(false, null));
    }
    public void save(String id, String title, String description, RescueReason reason,
            RescueMode mode, String targetsText, String startDate, String endDate,
            String latitude, String longitude, String locationName, boolean submit) {
        String validation = CampaignValidator.validate(title, targetsText, startDate, endDate,
                latitude, longitude);
        if (validation != null) { state.setValue(CampaignScreenState.error(validation)); return; }
        String sellerId = requireSeller(); if (sellerId == null) return;
        Map<String, Double> targets = CampaignValidator.parseTargets(targetsText);
        double target = 0D; for (double quantity : targets.values()) target += quantity;
        try {
            RescueCampaign campaign = new RescueCampaign(id, sellerId, target);
            campaign.defineCampaign(title, description, reason, mode, targets);
            campaign.schedule(CampaignValidator.parseDate(startDate), CampaignValidator.parseDate(endDate));
            campaign.updateFixedLocation(CampaignValidator.parseNumber(latitude),
                    CampaignValidator.parseNumber(longitude), locationName);
            state.setValue(CampaignScreenState.loading());
            repository.saveCampaign(campaign, submit, callback(true,
                    submit ? "Đã gửi campaign chờ duyệt." : "Đã lưu bản nháp."));
        } catch (IllegalArgumentException | IllegalStateException error) {
            state.setValue(CampaignScreenState.error(error.getMessage()));
        }
    }
    public void updateCurrentLocation(String campaignId) {
        state.setValue(CampaignScreenState.loading());
        locationProvider.getCurrentLocation(new LocationProvider.LocationCallback() {
            @Override public void onLocationAvailable(double latitude, double longitude) {
                repository.updateMobileLocation(campaignId, latitude, longitude, true,
                        new CampaignRepository.CampaignCallback() {
                            @Override public void onSuccess(RescueCampaign campaign) {
                                state.postValue(CampaignScreenState.location(campaign,
                                        "Đã cập nhật vị trí mobile rescue."));
                            }
                            @Override public void onError(CampaignRepository.ErrorCode error, String message) {
                                state.postValue(CampaignScreenState.error(message));
                            }
                        });
            }
            @Override public void onLocationUnavailable(String message) {
                state.postValue(CampaignScreenState.error(message));
            }
        });
    }
    public void stopLocationSharing(String campaignId) {
        repository.updateMobileLocation(campaignId, 0D, 0D, false,
                new CampaignRepository.CampaignCallback() {
                    @Override public void onSuccess(RescueCampaign campaign) {
                        state.postValue(CampaignScreenState.location(campaign, "Đã tắt chia sẻ vị trí."));
                    }
                    @Override public void onError(CampaignRepository.ErrorCode error, String message) {
                        state.postValue(CampaignScreenState.error(message));
                    }
                });
    }
    private CampaignRepository.CampaignCallback callback(boolean save, String message) {
        return new CampaignRepository.CampaignCallback() {
            @Override public void onSuccess(RescueCampaign campaign) {
                state.postValue(save ? CampaignScreenState.saved(campaign, message)
                        : CampaignScreenState.campaign(campaign));
            }
            @Override public void onError(CampaignRepository.ErrorCode error, String value) {
                state.postValue(CampaignScreenState.error(value));
            }
        };
    }
    private CampaignRepository.ActionCallback action(String message) {
        return new CampaignRepository.ActionCallback() {
            @Override public void onSuccess() { }
            @Override public void onError(CampaignRepository.ErrorCode error, String value) {
                state.postValue(CampaignScreenState.error(value));
            }
        };
    }
    private String currentUserId() {
        String id = authRepository.getCurrentUserId(); return id == null ? "" : id;
    }
    private String requireSeller() {
        String id = currentUserId();
        if (!authRepository.isAuthenticated() || id.isEmpty()) {
            state.setValue(CampaignScreenState.error("Vui lòng đăng nhập seller.")); return null;
        }
        return id;
    }
    @Override protected void onCleared() {
        repository.close(); locationProvider.close(); super.onCleared();
    }
}
