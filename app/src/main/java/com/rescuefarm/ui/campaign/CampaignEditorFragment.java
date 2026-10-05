package com.rescuefarm.ui.campaign;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.rescuefarm.R;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.model.RescueCampaign;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class CampaignEditorFragment extends Fragment {
    private String id = ""; private CampaignViewModel viewModel;
    private TextInputEditText title, description, targets, start, end, latitude, longitude, locationName;
    private Spinner reason, mode;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_campaign_editor, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        title = view.findViewById(R.id.campaignTitleInput); description = view.findViewById(R.id.campaignDescriptionInput);
        targets = view.findViewById(R.id.campaignTargetsInput); start = view.findViewById(R.id.campaignStartInput);
        end = view.findViewById(R.id.campaignEndInput); latitude = view.findViewById(R.id.campaignLatitudeInput);
        longitude = view.findViewById(R.id.campaignLongitudeInput); locationName = view.findViewById(R.id.campaignLocationNameInput);
        reason = view.findViewById(R.id.campaignReasonSpinner); mode = view.findViewById(R.id.campaignModeSpinner);
        reason.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, RescueReason.values()));
        mode.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, RescueMode.values()));
        id = getArguments() == null ? "" : getArguments().getString("campaignId", "");
        viewModel = new ViewModelProvider(this, new CampaignViewModelFactory(requireContext())).get(CampaignViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), value -> {
            if (value.getStatus() == CampaignScreenState.Status.CAMPAIGN) populate(value.getCampaign());
            else if (value.getStatus() == CampaignScreenState.Status.SAVED) {
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            } else if (value.getStatus() == CampaignScreenState.Status.ERROR)
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_LONG).show();
        });
        view.findViewById(R.id.saveCampaignDraftButton).setOnClickListener(v -> save(false));
        view.findViewById(R.id.submitCampaignButton).setOnClickListener(v -> save(true));
        if (!id.isEmpty()) viewModel.load(id);
    }
    private void save(boolean submit) {
        viewModel.save(id, text(title), text(description), (RescueReason) reason.getSelectedItem(),
                (RescueMode) mode.getSelectedItem(), text(targets), text(start), text(end),
                text(latitude), text(longitude), text(locationName), submit);
    }
    private void populate(RescueCampaign value) {
        title.setText(value.getTitle()); description.setText(value.getDescription());
        reason.setSelection(value.getRescueReason().ordinal()); mode.setSelection(value.getRescueMode().ordinal());
        StringBuilder batchText = new StringBuilder();
        for (Map.Entry<String, Double> item : value.getBatchTargets().entrySet()) {
            if (batchText.length() > 0) batchText.append('\n');
            batchText.append(item.getKey()).append('=').append(item.getValue());
        }
        targets.setText(batchText.toString());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        start.setText(format.format(value.getStartDate())); end.setText(format.format(value.getEndDate()));
        latitude.setText(String.valueOf(value.getLatitude())); longitude.setText(String.valueOf(value.getLongitude()));
        locationName.setText(value.getLocationName());
    }
    private static String text(TextInputEditText value) {
        return value.getText() == null ? "" : value.getText().toString();
    }
}
