package com.rescuefarm.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.rescuefarm.R;
import com.rescuefarm.domain.model.ProductBatch;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BatchManagerFragment extends Fragment {
    private ProductViewModel viewModel; private String productId, editingId = ""; private long editingVersion;
    private LinearLayout container; private TextInputEditText harvest, expiry, quantity;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_batch_manager, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        Bundle args = getArguments(); productId = args == null ? "" : args.getString("productId", "");
        ((TextView) view.findViewById(R.id.batchProductName)).setText(args == null ? "" : args.getString("productName", ""));
        container = view.findViewById(R.id.batchContainer); harvest = view.findViewById(R.id.harvestDateInput);
        expiry = view.findViewById(R.id.expiryDateInput); quantity = view.findViewById(R.id.batchQuantityInput);
        viewModel = new ViewModelProvider(this, new ProductViewModelFactory(requireContext()))
                .get(ProductViewModel.class);
        viewModel.getBatches(productId).observe(getViewLifecycleOwner(), this::render);
        viewModel.getState().observe(getViewLifecycleOwner(), value -> {
            if (value.getStatus() == ProductScreenState.Status.SAVED) {
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_SHORT).show();
                clearEditor(); viewModel.refreshBatches(productId);
            } else if (value.getStatus() == ProductScreenState.Status.ERROR)
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_LONG).show();
        });
        view.findViewById(R.id.saveBatchButton).setOnClickListener(v -> viewModel.saveBatch(editingId,
                productId, text(harvest), text(expiry), text(quantity), editingVersion));
        view.findViewById(R.id.cancelBatchEditButton).setOnClickListener(v -> clearEditor());
        viewModel.refreshBatches(productId);
    }
    private void render(List<ProductBatch> batches) {
        container.removeAllViews(); if (batches == null) return;
        for (ProductBatch batch : batches) container.addView(card(batch));
    }
    private View card(ProductBatch batch) {
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2); params.setMargins(0, 0, 0, dp(10)); card.setLayoutParams(params);
        card.setRadius(dp(14)); card.setStrokeWidth(dp(1));
        LinearLayout body = new LinearLayout(requireContext()); body.setOrientation(LinearLayout.VERTICAL); body.setPadding(dp(12), dp(12), dp(12), dp(12));
        TextView info = new TextView(requireContext()); info.setText(getString(R.string.batch_info_format,
                batch.getAvailableQuantity(), batch.getReservedQuantity(), batch.getSoldQuantity(),
                batch.getStatus().name(), batch.getInventoryVersion()));
        LinearLayout actions = new LinearLayout(requireContext());
        Button edit = new Button(requireContext()); edit.setText(R.string.edit_action); edit.setOnClickListener(v -> edit(batch));
        Button delete = new Button(requireContext()); delete.setText(R.string.delete_action);
        delete.setEnabled(batch.getReservedQuantity() == 0D && batch.getSoldQuantity() == 0D
                && (batch.getActiveCampaignId() == null || batch.getActiveCampaignId().isEmpty()));
        delete.setOnClickListener(v -> viewModel.deleteBatch(batch.getId(), batch.getInventoryVersion()));
        actions.addView(edit); actions.addView(delete); body.addView(info); body.addView(actions); card.addView(body); return card;
    }
    private void edit(ProductBatch batch) {
        editingId = batch.getId(); editingVersion = batch.getInventoryVersion();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        harvest.setText(batch.getHarvestDate() == null ? "" : format.format(batch.getHarvestDate()));
        expiry.setText(batch.getExpiryDate() == null ? "" : format.format(batch.getExpiryDate()));
        quantity.setText(String.valueOf(batch.getInitialQuantity()));
    }
    private void clearEditor() { editingId = ""; editingVersion = 0L; harvest.setText(""); expiry.setText(""); quantity.setText(""); }
    private static String text(TextInputEditText input) { return input.getText() == null ? "" : input.getText().toString(); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
