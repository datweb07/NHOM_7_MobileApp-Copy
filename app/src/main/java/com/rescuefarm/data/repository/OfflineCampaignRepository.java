package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.rescuefarm.data.local.dao.CampaignCacheDao;
import com.rescuefarm.data.local.entity.CampaignCacheEntity;
import com.rescuefarm.data.local.mapper.CampaignCacheMapper;
import com.rescuefarm.domain.model.RescueCampaign;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class OfflineCampaignRepository implements CampaignRepository {
    private static final String MESSAGE = "Firebase chưa được cấu hình; đang hiển thị cache chiến dịch.";
    private final CampaignCacheDao dao;
    private final ExecutorService executor;
    public OfflineCampaignRepository(CampaignCacheDao dao, ExecutorService executor) {
        this.dao = dao; this.executor = executor;
    }
    @Override public LiveData<List<RescueCampaign>> observeActiveCampaigns() {
        return Transformations.map(dao.observeActiveCampaigns(), CampaignCacheMapper::campaigns);
    }
    @Override public LiveData<List<RescueCampaign>> observeSellerCampaigns(String sellerId) {
        return Transformations.map(dao.observeSellerCampaigns(sellerId), CampaignCacheMapper::campaigns);
    }
    @Override public void refreshActiveCampaigns(ActionCallback callback) { unavailable(callback); }
    @Override public void refreshSellerCampaigns(String sellerId, ActionCallback callback) { unavailable(callback); }
    @Override public void getCampaign(String campaignId, CampaignCallback callback) {
        executor.execute(() -> {
            CampaignCacheEntity cached = dao.findCampaign(campaignId);
            if (cached == null) callback.onError(ErrorCode.NOT_FOUND, "Không có chiến dịch trong cache.");
            else try { callback.onSuccess(CampaignCacheMapper.toDomain(cached)); }
            catch (IllegalArgumentException | IllegalStateException error) {
                callback.onError(ErrorCode.NOT_FOUND, "Cache chiến dịch cũ không còn hợp lệ.");
            }
        });
    }
    @Override public void saveCampaign(RescueCampaign campaign, boolean submit, CampaignCallback callback) {
        callback.onError(ErrorCode.NOT_CONFIGURED, MESSAGE);
    }
    @Override public void updateMobileLocation(String id, double lat, double lng, boolean sharing,
            CampaignCallback callback) { callback.onError(ErrorCode.NOT_CONFIGURED, MESSAGE); }
    @Override public void mutateReservation(String campaignId, String batchId, long version,
            ReservationMutation mutation, double quantity, CampaignCallback callback) {
        callback.onError(ErrorCode.NOT_CONFIGURED, MESSAGE);
    }
    private void unavailable(ActionCallback callback) {
        callback.onError(ErrorCode.NOT_CONFIGURED, MESSAGE);
    }
    @Override public void close() { executor.shutdownNow(); }
}
