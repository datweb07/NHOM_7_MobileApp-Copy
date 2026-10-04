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
import com.google.android.material.textfield.TextInputEditText;
import com.rescuefarm.R;
import com.rescuefarm.domain.enums.ApplicationStatus;
import com.rescuefarm.domain.model.SellerApplication;

public class SellerApplicationFragment extends Fragment {
    private ProfileViewModel viewModel; private View form, submit;
    private TextView status; private TextInputEditText representative, shop, address, proof;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_seller_application, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        form = view.findViewById(R.id.sellerApplicationForm); submit = view.findViewById(R.id.submitApplicationButton);
        status = view.findViewById(R.id.applicationStatusText);
        representative = view.findViewById(R.id.representativeInput); shop = view.findViewById(R.id.shopNameInput);
        address = view.findViewById(R.id.shopAddressInput); proof = view.findViewById(R.id.proofImageInput);
        viewModel = new ViewModelProvider(this, new ProfileViewModelFactory(requireContext()))
                .get(ProfileViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), this::render);
        submit.setOnClickListener(v -> viewModel.submitSellerApplication(text(representative), text(shop),
                text(address), text(proof)));
        viewModel.loadSellerApplication();
    }
    private void render(ProfileScreenState state) {
        if (state.getStatus() == ProfileScreenState.Status.ERROR) {
            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_LONG).show(); return;
        }
        if (state.getStatus() != ProfileScreenState.Status.APPLICATION) return;
        SellerApplication application = state.getApplication();
        boolean editable = application == null || application.getStatus() == ApplicationStatus.REJECTED;
        form.setVisibility(editable ? View.VISIBLE : View.GONE);
        if (application == null) status.setText(R.string.application_not_submitted);
        else {
            status.setText(getString(R.string.application_status_format, application.getStatus().name()));
            if (editable) {
                representative.setText(application.getRepresentativeName()); shop.setText(application.getShopName());
                address.setText(application.getAddress()); proof.setText(application.getProofImageUrl());
            }
        }
    }
    private static String text(TextInputEditText input) { return input.getText() == null ? "" : input.getText().toString(); }
}
