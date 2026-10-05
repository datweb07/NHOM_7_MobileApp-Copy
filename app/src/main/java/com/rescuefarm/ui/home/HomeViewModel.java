package com.rescuefarm.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.rescuefarm.data.repository.AuthRepository;
import com.rescuefarm.data.repository.CampaignRepository;
import com.rescuefarm.data.repository.ProductRepository;
import com.rescuefarm.domain.model.Banner;
import com.rescuefarm.domain.model.Category;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.RescueCampaign;
import com.rescuefarm.service.location.LocationProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final CampaignRepository campaignRepository;
    private final AuthRepository authRepository;
    private final LocationProvider locationProvider;
    private final HomeContentBuilder contentBuilder = new HomeContentBuilder();
    private final DiscoveryEngine discoveryEngine = new DiscoveryEngine();
    private final MediatorLiveData<HomeViewState> homeState = new MediatorLiveData<>();
    private final MutableLiveData<List<DiscoveryResult>> searchResults =
            new MutableLiveData<>(new ArrayList<>());
    private List<Product> products = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<RescueCampaign> campaigns = new ArrayList<>();
    private List<Banner> banners = new ArrayList<>();
    private DiscoveryQuery query = DiscoveryQuery.empty();
    private Double latitude;
    private Double longitude;
    private HomeViewState.LocationState locationState = HomeViewState.LocationState.PERMISSION_REQUIRED;
    private boolean refreshing;
    private int pendingRefreshes;
    private boolean refreshHadError;
    private String message = "Đang hiển thị dữ liệu đã lưu trên thiết bị.";
    private HomeViewState.DataFreshness freshness = HomeViewState.DataFreshness.STALE;

    public HomeViewModel(ProductRepository productRepository, CampaignRepository campaignRepository,
            AuthRepository authRepository, LocationProvider locationProvider) {
        this.productRepository = productRepository; this.campaignRepository = campaignRepository;
        this.authRepository = authRepository; this.locationProvider = locationProvider;
        homeState.addSource(productRepository.observeProducts(), values -> {
            products = safe(values); publish();
        });
        homeState.addSource(productRepository.observeCategories(), values -> {
            categories = safe(values); publish();
        });
        homeState.addSource(campaignRepository.observeActiveCampaigns(), values -> {
            campaigns = safe(values); publish();
        });
        homeState.addSource(campaignRepository.observeActiveBanners(), values -> {
            banners = safe(values); publish();
        });
        publish();
    }

    public LiveData<HomeViewState> getHomeState() { return homeState; }
    public LiveData<List<DiscoveryResult>> getSearchResults() { return searchResults; }
    public List<Category> getCategoriesSnapshot() { return new ArrayList<>(categories); }
    public boolean isAuthenticated() { return authRepository.isAuthenticated(); }

    public void refresh() {
        refreshing = true; pendingRefreshes = 3; refreshHadError = false;
        message = "Đang đồng bộ dữ liệu mới…"; publish();
        productRepository.refreshCatalog(new ProductRepository.ActionCallback() {
            @Override public void onSuccess() { finishRefresh(null); }
            @Override public void onError(ProductRepository.ErrorCode error, String value) {
                finishRefresh(value);
            }
        });
        campaignRepository.refreshActiveCampaigns(callback());
        campaignRepository.refreshBanners(callback());
    }

    public void requestNearbyLocation() {
        locationState = HomeViewState.LocationState.LOADING; message = "Đang lấy vị trí hiện tại…";
        publish();
        locationProvider.getCurrentLocation(new LocationProvider.LocationCallback() {
            @Override public void onLocationAvailable(double valueLatitude, double valueLongitude) {
                latitude = valueLatitude; longitude = valueLongitude;
                locationState = HomeViewState.LocationState.AVAILABLE;
                message = "Đã sắp xếp điểm giải cứu gần vị trí hiện tại."; publish();
            }
            @Override public void onLocationUnavailable(String value) {
                locationState = HomeViewState.LocationState.UNAVAILABLE; message = value; publish();
            }
        });
    }

    public void onLocationPermissionDenied() {
        locationState = HomeViewState.LocationState.DENIED;
        message = "Chưa có quyền vị trí; các mục gần bạn được để trống."; publish();
    }

    public void setQuery(DiscoveryQuery value) {
        query = value == null ? DiscoveryQuery.empty() : value; publishSearch();
    }

    private CampaignRepository.ActionCallback callback() {
        return new CampaignRepository.ActionCallback() {
            @Override public void onSuccess() { finishRefresh(null); }
            @Override public void onError(CampaignRepository.ErrorCode error, String value) {
                finishRefresh(value);
            }
        };
    }
    private synchronized void finishRefresh(String error) {
        if (error != null && !error.trim().isEmpty()) {
            refreshHadError = true; freshness = HomeViewState.DataFreshness.OFFLINE;
            message = error + " Dữ liệu cache vẫn được giữ và chỉ dùng để tham khảo.";
        }
        pendingRefreshes = Math.max(0, pendingRefreshes - 1);
        if (pendingRefreshes == 0) {
            refreshing = false;
            if (!refreshHadError) { freshness = HomeViewState.DataFreshness.FRESH;
                message = "Đã cập nhật Home."; }
        }
        publish();
    }
    private void publish() {
        homeState.postValue(contentBuilder.build(banners, products, campaigns, categories,
                latitude, longitude, new Date(), locationState, refreshing, message, freshness));
        publishSearch();
    }
    private void publishSearch() {
        searchResults.postValue(discoveryEngine.search(products, campaigns, query,
                latitude, longitude, new Date()));
    }
    private static <T> List<T> safe(List<T> values) {
        return values == null ? new ArrayList<>() : new ArrayList<>(values);
    }
    @Override protected void onCleared() {
        productRepository.close(); campaignRepository.close(); locationProvider.close();
    }
}
