package com.rescuefarm.ui.profile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.rescuefarm.R;
import com.rescuefarm.domain.model.User;
import java.util.Map;

public class EditProfileFragment extends Fragment {
    private ProfileViewModel viewModel;
    private TextInputEditText nameInput, phoneInput, avatarInput;
    private TextView locationView;
    private double latitude, longitude;
    private boolean populated;
    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), this::onPermissionResult);

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        nameInput = view.findViewById(R.id.editFullNameInput);
        phoneInput = view.findViewById(R.id.editPhoneInput);
        avatarInput = view.findViewById(R.id.editAvatarInput);
        locationView = view.findViewById(R.id.profileLocationText);
        viewModel = new ViewModelProvider(this, new ProfileViewModelFactory(requireContext()))
                .get(ProfileViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), this::render);
        view.findViewById(R.id.useProfileLocationButton).setOnClickListener(v -> requestLocation());
        view.findViewById(R.id.saveProfileButton).setOnClickListener(v -> viewModel.updateProfile(
                text(nameInput), text(phoneInput), text(avatarInput), latitude, longitude));
        viewModel.loadProfile();
    }

    private void render(ProfileScreenState state) {
        if (state.getStatus() == ProfileScreenState.Status.PROFILE && !populated) {
            User user = state.getUser(); populated = true;
            nameInput.setText(user.getFullName()); phoneInput.setText(user.getPhone());
            avatarInput.setText(user.getAvatarUrl()); latitude = user.getLatitude(); longitude = user.getLongitude();
            showCoordinates();
        } else if (state.getStatus() == ProfileScreenState.Status.LOCATION) {
            latitude = state.getLatitude(); longitude = state.getLongitude(); showCoordinates();
        } else if (state.getStatus() == ProfileScreenState.Status.SAVED) {
            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        } else if (state.getStatus() == ProfileScreenState.Status.ERROR) {
            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void requestLocation() {
        if (hasLocationPermission()) viewModel.requestCurrentLocation(false);
        else permissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
    }
    private void onPermissionResult(Map<String, Boolean> result) {
        if (Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION))
                || Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION))) {
            viewModel.requestCurrentLocation(false);
        } else viewModel.onLocationPermissionDenied();
    }
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    private void showCoordinates() { locationView.setText(getString(R.string.coordinates_format, latitude, longitude)); }
    private static String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }
}
