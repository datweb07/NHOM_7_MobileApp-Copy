package com.rescuefarm.ui.home;

import com.rescuefarm.domain.model.Banner;
import com.rescuefarm.domain.model.Category;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.RescueCampaign;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HomeViewState {
    public enum LocationState { PERMISSION_REQUIRED, LOADING, AVAILABLE, UNAVAILABLE, DENIED }
    public enum Section {
        BANNERS, CRITICAL, NEARBY_MOBILE, NEARBY_FIXED, ENDING_SOON,
        VALUE_PRODUCTS, ACTIVE_CAMPAIGNS, CATEGORIES, RESCUE_FEED
    }
    public static final List<Section> SECTION_ORDER = Collections.unmodifiableList(Arrays.asList(
            Section.BANNERS, Section.CRITICAL, Section.NEARBY_MOBILE, Section.NEARBY_FIXED,
            Section.ENDING_SOON, Section.VALUE_PRODUCTS, Section.ACTIVE_CAMPAIGNS,
            Section.CATEGORIES, Section.RESCUE_FEED));

    private final List<Banner> banners;
    private final List<RescueCampaign> critical;
    private final List<RescueCampaign> nearbyMobile;
    private final List<RescueCampaign> nearbyFixed;
    private final List<RescueCampaign> endingSoon;
    private final List<Product> valueProducts;
    private final List<RescueCampaign> activeCampaigns;
    private final List<Category> categories;
    private final Map<String, Double> distances;
    private final LocationState locationState;
    private final boolean refreshing;
    private final String message;

    public HomeViewState(List<Banner> banners, List<RescueCampaign> critical,
            List<RescueCampaign> nearbyMobile, List<RescueCampaign> nearbyFixed,
            List<RescueCampaign> endingSoon, List<Product> valueProducts,
            List<RescueCampaign> activeCampaigns, List<Category> categories,
            Map<String, Double> distances, LocationState locationState, boolean refreshing,
            String message) {
        this.banners = copy(banners); this.critical = copy(critical);
        this.nearbyMobile = copy(nearbyMobile); this.nearbyFixed = copy(nearbyFixed);
        this.endingSoon = copy(endingSoon); this.valueProducts = copy(valueProducts);
        this.activeCampaigns = copy(activeCampaigns); this.categories = copy(categories);
        this.distances = Collections.unmodifiableMap(new LinkedHashMap<>(distances));
        this.locationState = locationState; this.refreshing = refreshing;
        this.message = message == null ? "" : message;
    }
    private static <T> List<T> copy(List<T> values) {
        return Collections.unmodifiableList(new ArrayList<>(values));
    }
    public List<Banner> getBanners() { return banners; }
    public List<RescueCampaign> getCritical() { return critical; }
    public List<RescueCampaign> getNearbyMobile() { return nearbyMobile; }
    public List<RescueCampaign> getNearbyFixed() { return nearbyFixed; }
    public List<RescueCampaign> getEndingSoon() { return endingSoon; }
    public List<Product> getValueProducts() { return valueProducts; }
    public List<RescueCampaign> getActiveCampaigns() { return activeCampaigns; }
    public List<Category> getCategories() { return categories; }
    public double distanceFor(String campaignId) {
        Double value = distances.get(campaignId); return value == null ? Double.NaN : value;
    }
    public LocationState getLocationState() { return locationState; }
    public boolean isRefreshing() { return refreshing; }
    public String getMessage() { return message; }
}
