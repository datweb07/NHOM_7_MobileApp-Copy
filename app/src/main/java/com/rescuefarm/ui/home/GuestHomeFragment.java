package com.rescuefarm.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.rescuefarm.R;
import com.rescuefarm.domain.model.Banner;
import com.rescuefarm.domain.model.Category;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.RescueCampaign;
import java.util.List;
import java.util.Map;

public class GuestHomeFragment extends Fragment {
    private HomeViewModel viewModel;
    private HomeCardRenderer renderer;
    private TextView message;
    private ProgressBar progress;
    private LinearLayout bannerSection, criticalSection, mobileSection, fixedSection,
            endingSection, valueSection, campaignSection, categorySection, feedSection;
    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), this::onPermissions);

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guest_home, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state); renderer = new HomeCardRenderer(this);
        message = view.findViewById(R.id.homeMessage); progress = view.findViewById(R.id.homeProgress);
        bannerSection = view.findViewById(R.id.bannerSection);
        criticalSection = view.findViewById(R.id.criticalSection);
        mobileSection = view.findViewById(R.id.mobileSection);
        fixedSection = view.findViewById(R.id.fixedSection);
        endingSection = view.findViewById(R.id.endingSection);
        valueSection = view.findViewById(R.id.valueSection);
        campaignSection = view.findViewById(R.id.campaignSection);
        categorySection = view.findViewById(R.id.categorySection);
        feedSection = view.findViewById(R.id.feedSection);
        viewModel = new ViewModelProvider(this, new HomeViewModelFactory(requireContext()))
                .get(HomeViewModel.class);
        viewModel.getHomeState().observe(getViewLifecycleOwner(), this::render);

        View login = view.findViewById(R.id.loginButton); View profile = view.findViewById(R.id.profileButton);
        login.setVisibility(viewModel.isAuthenticated() ? View.GONE : View.VISIBLE);
        profile.setVisibility(viewModel.isAuthenticated() ? View.VISIBLE : View.GONE);
        login.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_guestHomeFragment_to_loginFragment));
        profile.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_guestHomeFragment_to_profileFragment));
        view.findViewById(R.id.searchButton).setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_guestHomeFragment_to_discoveryFragment));
        view.findViewById(R.id.refreshHomeButton).setOnClickListener(v -> viewModel.refresh());
        view.findViewById(R.id.locationHomeButton).setOnClickListener(v -> requestLocation());
        viewModel.refresh();
    }

    private void requestLocation() {
        boolean fine = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        if (fine || coarse) viewModel.requestNearbyLocation();
        else permissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
    }
    private void onPermissions(Map<String, Boolean> values) {
        if (Boolean.TRUE.equals(values.get(Manifest.permission.ACCESS_FINE_LOCATION))
                || Boolean.TRUE.equals(values.get(Manifest.permission.ACCESS_COARSE_LOCATION))) {
            viewModel.requestNearbyLocation();
        } else viewModel.onLocationPermissionDenied();
    }

    private void render(HomeViewState state) {
        message.setText(state.getMessage()); progress.setVisibility(state.isRefreshing() ? View.VISIBLE : View.GONE);
        renderBanners(state.getBanners());
        renderCampaigns(criticalSection, state.getCritical(), state, "Chưa có chiến dịch CRITICAL.");
        String nearbyEmpty = state.getLocationState() == HomeViewState.LocationState.AVAILABLE
                ? "Không có điểm phù hợp gần vị trí hiện tại." : "Bấm ‘Dùng vị trí’ để xem mục này.";
        renderCampaigns(mobileSection, state.getNearbyMobile(), state, nearbyEmpty);
        renderCampaigns(fixedSection, state.getNearbyFixed(), state, nearbyEmpty);
        renderCampaigns(endingSection, state.getEndingSoon(), state, "Chưa có chiến dịch sắp kết thúc.");
        renderProducts(state.getValueProducts());
        renderCampaigns(campaignSection, state.getActiveCampaigns(), state, "Chưa có chiến dịch ACTIVE trong cache.");
        renderCategories(state.getCategories());
        feedSection.removeAllViews(); feedSection.addView(renderer.message(
                "Feed cộng đồng sẽ được kết nối ở Phase 7; Home giữ đúng vị trí section này."));
    }
    private void renderBanners(List<Banner> values) {
        bannerSection.removeAllViews();
        if (values.isEmpty()) { bannerSection.addView(renderer.message("Chưa có banner đang hiệu lực.")); return; }
        for (Banner value : values) bannerSection.addView(renderer.banner(value, v -> {
            if (!value.getCampaignId().isEmpty()) openCampaign(value.getCampaignId());
        }));
    }
    private void renderCampaigns(LinearLayout container, List<RescueCampaign> values,
            HomeViewState state, String empty) {
        container.removeAllViews();
        if (values.isEmpty()) { container.addView(renderer.message(empty)); return; }
        for (RescueCampaign value : values) container.addView(renderer.campaign(value,
                state.distanceFor(value.getId()), v -> openCampaign(value.getId())));
    }
    private void renderProducts(List<Product> values) {
        valueSection.removeAllViews();
        if (values.isEmpty()) { valueSection.addView(renderer.message("Chưa có nông sản giảm giá trong cache.")); return; }
        for (Product value : values) valueSection.addView(renderer.product(value, v -> openProduct(value.getId())));
    }
    private void renderCategories(List<Category> values) {
        categorySection.removeAllViews();
        if (values.isEmpty()) { categorySection.addView(renderer.message("Chưa có danh mục trong cache.")); return; }
        for (Category value : values) categorySection.addView(renderer.category(value, v -> {
            Bundle args = new Bundle(); args.putString("categoryId", value.getId());
            Navigation.findNavController(requireView()).navigate(
                    R.id.action_guestHomeFragment_to_discoveryFragment, args);
        }));
    }
    private void openCampaign(String id) {
        Bundle args = new Bundle(); args.putString("campaignId", id);
        Navigation.findNavController(requireView()).navigate(
                R.id.action_guestHomeFragment_to_campaignDetailFragment, args);
    }
    private void openProduct(String id) {
        Bundle args = new Bundle(); args.putString("productId", id);
        Navigation.findNavController(requireView()).navigate(
                R.id.action_guestHomeFragment_to_productDetailFragment, args);
    }
}
