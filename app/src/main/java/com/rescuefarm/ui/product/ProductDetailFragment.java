package com.rescuefarm.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.rescuefarm.R;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.ProductBatch;
import com.rescuefarm.service.pricing.PriceBreakdown;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {
    private ProductViewModel viewModel; private String productId; private TextView stock;
    private Product currentProduct; private EditText quantityInput;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_product_detail, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        productId = getArguments() == null ? "" : getArguments().getString("productId", "");
        stock = view.findViewById(R.id.productStock);
        quantityInput = view.findViewById(R.id.pricingQuantityInput);
        viewModel = new ViewModelProvider(this, new ProductViewModelFactory(requireContext()))
                .get(ProductViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), value -> {
            if (value.getStatus() == ProductScreenState.Status.PRODUCT) render(view, value.getProduct());
            else if (value.getStatus() == ProductScreenState.Status.ERROR)
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_LONG).show();
        });
        viewModel.getBatches(productId).observe(getViewLifecycleOwner(), this::renderStock);
        viewModel.getPriceBreakdown().observe(getViewLifecycleOwner(), value -> renderPrice(view, value));
        view.findViewById(R.id.recalculatePriceButton).setOnClickListener(v -> recalculate());
        viewModel.loadProduct(productId); viewModel.refreshBatches(productId);
    }
    private void render(View view, Product product) {
        currentProduct = product;
        ((TextView) view.findViewById(R.id.productDetailName)).setText(product.getName());
        ((TextView) view.findViewById(R.id.productDetailDescription)).setText(product.getDescription());
        ((TextView) view.findViewById(R.id.productDetailPrice)).setText(
                NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(product.getRescuePrice())
                        + " / " + product.getUnit());
        ((TextView) view.findViewById(R.id.productDetailOrigin)).setText(product.getOrigin() + " • " + product.getProvince());
        ((TextView) view.findViewById(R.id.productDiscount)).setText(
                getString(R.string.discount_format, product.calculateDiscountPercent()));
        if (!product.getImageUrls().isEmpty()) Glide.with(this).load(product.getImageUrls().get(0))
                .centerCrop().into((ImageView) view.findViewById(R.id.productDetailImage));
        viewModel.calculatePrice(product, 1D);
    }
    private void recalculate() {
        if (currentProduct == null) return;
        try {
            double quantity = Double.parseDouble(quantityInput.getText().toString().trim());
            viewModel.calculatePrice(currentProduct, quantity);
        } catch (NumberFormatException error) {
            Toast.makeText(requireContext(), R.string.invalid_pricing_quantity, Toast.LENGTH_SHORT).show();
        }
    }
    private void renderPrice(View view, PriceBreakdown value) {
        if (value == null) return;
        NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String text = getString(R.string.price_breakdown_format,
                money.format(value.getOriginalPrice()), money.format(value.getRescuePrice()),
                money.format(value.getPromotionDiscount()), value.getVolumeDiscountPercent(),
                money.format(value.getFinalUnitPrice()), money.format(value.getLineSubtotal()));
        ((TextView) view.findViewById(R.id.priceBreakdownText)).setText(text);
    }
    private void renderStock(List<ProductBatch> batches) {
        double available = 0D; int sellable = 0;
        if (batches != null) for (ProductBatch batch : batches) if (batch.isSellable(new java.util.Date())) {
            available += batch.getAvailableQuantity(); sellable++;
        }
        stock.setText(getString(R.string.product_stock_format, available, sellable));
    }
}
