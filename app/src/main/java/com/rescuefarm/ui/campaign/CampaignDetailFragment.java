package com.rescuefarm.ui.campaign;

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
import com.rescuefarm.R;
import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.model.RescueCampaign;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CampaignDetailFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_campaign_detail, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        String id = getArguments() == null ? "" : getArguments().getString("campaignId", "");
        CampaignViewModel viewModel = new ViewModelProvider(this,
                new CampaignViewModelFactory(requireContext())).get(CampaignViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), value -> {
            if (value.getStatus() == CampaignScreenState.Status.CAMPAIGN) render(view, value.getCampaign());
            else if (value.getStatus() == CampaignScreenState.Status.ERROR)
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_LONG).show();
        });
        viewModel.load(id);
    }
    private void render(View view, RescueCampaign value) {
        set(view, R.id.campaignDetailTitle, value.getTitle());
        set(view, R.id.campaignDetailBadge, value.getHighlightLabel());
        set(view, R.id.campaignDetailDescription, value.getDescription());
        set(view, R.id.campaignDetailReason, getString(R.string.campaign_reason_format,
                value.getRescueReason().name(), value.getUrgencyLevel().name()));
        set(view, R.id.campaignDetailProgress, getString(R.string.campaign_progress_format,
                value.calculateProgress(), value.getRescuedQuantity(), value.getTargetQuantity()));
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));
        set(view, R.id.campaignDetailSchedule, format.format(value.getStartDate()) + " – " + format.format(value.getEndDate()));
        String location = value.getLocationName();
        if (value.getRescueMode() == RescueMode.MOBILE_POINT) {
            location = value.isLocationFresh(new Date())
                    ? getString(R.string.mobile_location_fresh)
                    : getString(R.string.mobile_location_stale);
        }
        set(view, R.id.campaignDetailLocation, location);
    }
    private static void set(View root, int id, String value) {
        ((TextView) root.findViewById(id)).setText(value == null ? "" : value);
    }
}
