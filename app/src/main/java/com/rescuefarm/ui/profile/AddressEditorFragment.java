package com.rescuefarm.ui.profile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
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
import com.rescuefarm.domain.enums.AddressType;
import java.util.Map;

public class AddressEditorFragment extends Fragment {
    private ProfileViewModel viewModel;
    private TextInputEditText name, phone, province, district, ward, street;
    private RadioGroup typeGroup; private CheckBox defaultCheck; private TextView locationText;
    private String id = ""; private double latitude, longitude;
    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), this::onPermissionResult);

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_address_editor, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        name = view.findViewById(R.id.receiverNameInput); phone = view.findViewById(R.id.receiverPhoneInput);
        province = view.findViewById(R.id.provinceInput); district = view.findViewById(R.id.districtInput);
        ward = view.findViewById(R.id.wardInput); street = view.findViewById(R.id.streetInput);
        typeGroup = view.findViewById(R.id.addressTypeGroup); defaultCheck = view.findViewById(R.id.defaultAddressCheck);
        locationText = view.findViewById(R.id.addressLocationText);
        readArguments();
        viewModel = new ViewModelProvider(this, new ProfileViewModelFactory(requireContext()))
                .get(ProfileViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), this::render);
        view.findViewById(R.id.useAddressLocationButton).setOnClickListener(v -> requestLocation());
        view.findViewById(R.id.saveAddressButton).setOnClickListener(v -> viewModel.saveAddress(
                id, text(name), text(phone), text(province), text(district), text(ward), text(street),
                latitude, longitude, selectedType(), defaultCheck.isChecked()));
    }

    private void readArguments() {
        Bundle args = getArguments(); if (args == null) return;
        id = safe(args.getString("id")); name.setText(args.getString("receiverName"));
        phone.setText(args.getString("receiverPhone")); province.setText(args.getString("province"));
        district.setText(args.getString("district")); ward.setText(args.getString("ward"));
        street.setText(args.getString("street")); latitude = args.getDouble("latitude");
        longitude = args.getDouble("longitude"); defaultCheck.setChecked(args.getBoolean("isDefault"));
        String type = args.getString("type");
        if (AddressType.WORK.name().equals(type)) typeGroup.check(R.id.addressTypeWork);
        else if (AddressType.OTHER.name().equals(type)) typeGroup.check(R.id.addressTypeOther);
        showCoordinates();
    }
    private void render(ProfileScreenState state) {
        if (state.getStatus() == ProfileScreenState.Status.LOCATION) {
            latitude = state.getLatitude(); longitude = state.getLongitude(); showCoordinates();
            if (state.getGeocodedAddress() != null) {
                province.setText(state.getGeocodedAddress().getProvince());
                district.setText(state.getGeocodedAddress().getDistrict());
                ward.setText(state.getGeocodedAddress().getWard()); street.setText(state.getGeocodedAddress().getStreet());
            }
            if (state.getMessage() != null) {
                Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (state.getStatus() == ProfileScreenState.Status.SAVED) {
            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();
        } else if (state.getStatus() == ProfileScreenState.Status.ERROR) {
            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void requestLocation() {
        if (hasPermission()) viewModel.requestCurrentLocation(true);
        else permissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
    }
    private void onPermissionResult(Map<String, Boolean> result) {
        if (Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION))
                || Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION))) {
            viewModel.requestCurrentLocation(true);
        } else viewModel.onLocationPermissionDenied();
    }
    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private AddressType selectedType() {
        if (typeGroup.getCheckedRadioButtonId() == R.id.addressTypeWork) return AddressType.WORK;
        if (typeGroup.getCheckedRadioButtonId() == R.id.addressTypeOther) return AddressType.OTHER;
        return AddressType.HOME;
    }
    private void showCoordinates() { locationText.setText(getString(R.string.coordinates_format, latitude, longitude)); }
    private static String text(TextInputEditText input) { return input.getText() == null ? "" : input.getText().toString(); }
    private static String safe(String value) { return value == null ? "" : value; }
}
