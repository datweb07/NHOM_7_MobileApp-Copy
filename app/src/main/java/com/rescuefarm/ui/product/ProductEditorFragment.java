package com.rescuefarm.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.rescuefarm.R;
import com.rescuefarm.domain.enums.ProductStatus;
import com.rescuefarm.domain.model.Category;
import java.util.List;

public class ProductEditorFragment extends Fragment {
    private ProductViewModel viewModel; private String id = "";
    private TextInputEditText category, name, description, originalPrice, rescuePrice, unit, origin, province, images;
    private RadioGroup statusGroup;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_product_editor, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        category = view.findViewById(R.id.productCategoryInput); name = view.findViewById(R.id.productNameInput);
        description = view.findViewById(R.id.productDescriptionInput); originalPrice = view.findViewById(R.id.originalPriceInput);
        rescuePrice = view.findViewById(R.id.rescuePriceInput); unit = view.findViewById(R.id.productUnitInput);
        origin = view.findViewById(R.id.productOriginInput); province = view.findViewById(R.id.productProvinceInput);
        images = view.findViewById(R.id.productImagesInput); statusGroup = view.findViewById(R.id.productStatusGroup);
        readArguments();
        viewModel = new ViewModelProvider(this, new ProductViewModelFactory(requireContext()))
                .get(ProductViewModel.class);
        viewModel.getCategories().observe(getViewLifecycleOwner(), values -> showCategories(view, values));
        viewModel.getState().observe(getViewLifecycleOwner(), value -> {
            if (value.getStatus() == ProductScreenState.Status.SAVED) {
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            } else if (value.getStatus() == ProductScreenState.Status.ERROR)
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_LONG).show();
        });
        view.findViewById(R.id.saveProductButton).setOnClickListener(v -> viewModel.saveProduct(id,
                text(category), text(name), text(description), text(originalPrice), text(rescuePrice),
                text(unit), text(origin), text(province), text(images), selectedStatus()));
        viewModel.refreshCatalog();
    }
    private void readArguments() {
        Bundle b = getArguments(); if (b == null) return; id = safe(b.getString("id"));
        category.setText(b.getString("categoryId")); name.setText(b.getString("name"));
        description.setText(b.getString("description"));
        if (b.containsKey("originalPrice")) originalPrice.setText(String.valueOf(b.getDouble("originalPrice")));
        if (b.containsKey("rescuePrice")) rescuePrice.setText(String.valueOf(b.getDouble("rescuePrice")));
        unit.setText(b.getString("unit")); origin.setText(b.getString("origin")); province.setText(b.getString("province"));
        images.setText(b.getString("imageUrls"));
        if (ProductStatus.INACTIVE.name().equals(b.getString("status"))) statusGroup.check(R.id.productStatusInactive);
    }
    private void showCategories(View view, List<Category> values) {
        StringBuilder text = new StringBuilder(getString(R.string.available_categories_label));
        if (values != null) for (Category value : values) text.append("\n• ").append(value.getName()).append(": ").append(value.getId());
        ((TextView) view.findViewById(R.id.availableCategoriesText)).setText(text.toString());
    }
    private ProductStatus selectedStatus() {
        return statusGroup.getCheckedRadioButtonId() == R.id.productStatusInactive
                ? ProductStatus.INACTIVE : ProductStatus.ACTIVE;
    }
    private static String text(TextInputEditText input) { return input.getText() == null ? "" : input.getText().toString(); }
    private static String safe(String value) { return value == null ? "" : value; }
}
