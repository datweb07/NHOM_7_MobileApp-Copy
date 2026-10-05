package com.rescuefarm.data.repository.admin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import com.rescuefarm.data.repository.AdminRepository.Section;

public final class AdminDashboardSnapshot {
    private final Map<Section, Integer> totals;
    private final int pendingSellers, pendingPosts, pendingCampaigns, pendingReviews, openReports;
    private final double deliveredRevenue;

    public AdminDashboardSnapshot(Map<Section, Integer> totals, int pendingSellers,
            int pendingPosts, int pendingCampaigns, int pendingReviews, int openReports,
            double deliveredRevenue) {
        EnumMap<Section, Integer> copy = new EnumMap<>(Section.class);
        if (totals != null) copy.putAll(totals);
        this.totals = Collections.unmodifiableMap(copy);
        this.pendingSellers = pendingSellers;
        this.pendingPosts = pendingPosts;
        this.pendingCampaigns = pendingCampaigns;
        this.pendingReviews = pendingReviews;
        this.openReports = openReports;
        this.deliveredRevenue = Math.max(0D, deliveredRevenue);
    }

    public int total(Section section) { Integer value = totals.get(section); return value == null ? 0 : value; }
    public int getPendingSellers() { return pendingSellers; }
    public int getPendingPosts() { return pendingPosts; }
    public int getPendingCampaigns() { return pendingCampaigns; }
    public int getPendingReviews() { return pendingReviews; }
    public int getOpenReports() { return openReports; }
    public double getDeliveredRevenue() { return deliveredRevenue; }
    public int getPendingModerationTotal() {
        return pendingSellers + pendingPosts + pendingCampaigns + pendingReviews + openReports;
    }
}
