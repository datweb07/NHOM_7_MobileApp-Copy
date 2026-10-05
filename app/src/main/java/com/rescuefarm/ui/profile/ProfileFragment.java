package com.rescuefarm.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.rescuefarm.R;
import com.rescuefarm.domain.enums.UserRole;
import com.rescuefarm.domain.model.Seller;
import com.rescuefarm.domain.model.User;

public class ProfileFragment extends Fragment {
    private ProfileViewModel viewModel;
    private TextView nameView, emailView, roleView, statusView;
    private View progress, addressButton, sellerButton, favoritesButton, adminReportsButton;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        nameView = view.findViewById(R.id.profileName);
        emailView = view.findViewById(R.id.profileEmail);
        roleView = view.findViewById(R.id.profileRole);
        statusView = view.findViewById(R.id.profileStatus);
        progress = view.findViewById(R.id.profileProgress);
        addressButton = view.findViewById(R.id.addressesButton);
        sellerButton = view.findViewById(R.id.sellerProfileButton);
        favoritesButton = view.findViewById(R.id.favoritesButton);
        adminReportsButton = view.findViewById(R.id.adminReportsButton);
        view.findViewById(R.id.editProfileButton).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_profileFragment_to_editProfileFragment));
        addressButton.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_profileFragment_to_addressListFragment));
        sellerButton.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_profileFragment_to_sellerProfileFragment));
        favoritesButton.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_profileFragment_to_favoriteFragment));
        view.findViewById(R.id.notificationsButton).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_profileFragment_to_notificationFragment));
        adminReportsButton.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_profileFragment_to_adminDashboardFragment));
        viewModel = new ViewModelProvider(this, new ProfileViewModelFactory(requireContext()))
                .get(ProfileViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), value -> render(value));
        viewModel.loadProfile();
    }

    private void render(ProfileScreenState state) {
        progress.setVisibility(state.getStatus() == ProfileScreenState.Status.LOADING ? View.VISIBLE : View.GONE);
        if (state.getStatus() == ProfileScreenState.Status.ERROR) {
            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_LONG).show(); return;
        }
        if (state.getStatus() != ProfileScreenState.Status.PROFILE) return;
        User user = state.getUser();
        nameView.setText(user.getFullName()); emailView.setText(user.getEmail());
        roleView.setText(user.getRole().name());
        addressButton.setVisibility(user.getRole() == UserRole.CUSTOMER ? View.VISIBLE : View.GONE);
        sellerButton.setVisibility(user.getRole() == UserRole.SELLER ? View.VISIBLE : View.GONE);
        favoritesButton.setVisibility(user.getRole() == UserRole.CUSTOMER ? View.VISIBLE : View.GONE);
        adminReportsButton.setVisibility(user.getRole() == UserRole.ADMIN ? View.VISIBLE : View.GONE);
        if (user instanceof Seller) {
            Seller seller = (Seller) user;
            statusView.setText(seller.canSell()
                    ? R.string.seller_approved_message : R.string.seller_pending_message);
        } else statusView.setText(user.getRole() == UserRole.ADMIN
                ? R.string.admin_access_message : R.string.customer_profile_message);
    }
}
