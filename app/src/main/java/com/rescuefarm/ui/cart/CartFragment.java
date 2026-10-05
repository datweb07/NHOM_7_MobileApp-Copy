package com.rescuefarm.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.rescuefarm.R;
import com.rescuefarm.domain.model.Cart;
import com.rescuefarm.domain.model.CartItem;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartFragment extends Fragment {
    private CartViewModel viewModel; private LinearLayout groups;
    private TextView summary; private TextView sync; private boolean initialRevalidation;
    private final NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_cart, parent, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        groups = view.findViewById(R.id.cartSellerGroups); summary = view.findViewById(R.id.cartSummaryText);
        sync = view.findViewById(R.id.cartSyncText);
        viewModel = new ViewModelProvider(this, new CartViewModelFactory(requireContext()))
                .get(CartViewModel.class);
        ((TextView) view.findViewById(R.id.cartOwnerText)).setText(viewModel.isGuestCart()
                ? R.string.guest_cart_storage_notice : R.string.customer_cart_storage_notice);
        viewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            render(cart);
            if (!initialRevalidation && cart != null) {
                initialRevalidation = true; viewModel.revalidateCurrentCart();
            }
        });
        viewModel.getState().observe(getViewLifecycleOwner(), value -> {
            sync.setText(value.getMessage());
            if (value.getStatus() == CartScreenState.Status.ERROR)
                Toast.makeText(requireContext(), value.getMessage(), Toast.LENGTH_LONG).show();
        });
        view.findViewById(R.id.cartRefreshButton).setOnClickListener(v -> viewModel.refreshAndRevalidate());
        view.findViewById(R.id.cartClearButton).setOnClickListener(v -> viewModel.clear());
        view.findViewById(R.id.cartCheckoutButton).setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_cartFragment_to_checkoutFragment));
        viewModel.refreshAndRevalidate();
    }
    private void render(Cart cart) {
        groups.removeAllViews();
        if (cart == null || cart.getItems().isEmpty()) {
            TextView empty = new TextView(requireContext()); empty.setText(R.string.cart_empty);
            groups.addView(empty); summary.setText(getString(R.string.cart_summary_format, 0, 0, money.format(0)));
            return;
        }
        Map<String, List<CartItem>> bySeller = new LinkedHashMap<>();
        for (CartItem item : cart.getItems()) bySeller.computeIfAbsent(item.getSellerId(),
                ignored -> new ArrayList<>()).add(item);
        for (Map.Entry<String, List<CartItem>> seller : bySeller.entrySet()) groups.addView(sellerCard(seller));
        int selected = 0; for (CartItem item : cart.getItems()) if (item.isSelected()) selected++;
        summary.setText(getString(R.string.cart_summary_format, selected,
                viewModel.groupSelectedBySeller(cart).size(), money.format(cart.calculateSubtotal())));
    }
    private View sellerCard(Map.Entry<String, List<CartItem>> seller) {
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.setMargins(0, 0, 0, dp(12)); card.setLayoutParams(cardParams); card.setRadius(dp(16));
        LinearLayout body = new LinearLayout(requireContext()); body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(14), dp(14), dp(14), dp(14));
        TextView title = new TextView(requireContext()); title.setText(getString(R.string.cart_seller_format,
                seller.getKey())); title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD); body.addView(title);
        for (CartItem item : seller.getValue()) body.addView(itemRow(item));
        card.addView(body); return card;
    }
    private View itemRow(CartItem item) {
        LinearLayout row = new LinearLayout(requireContext()); row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dp(10), 0, dp(10));
        CheckBox selected = new CheckBox(requireContext());
        selected.setText(item.getProductId() + " • batch " + item.getBatchId());
        selected.setChecked(item.isSelected()); selected.setOnCheckedChangeListener((button, checked) ->
                viewModel.setSelected(item.getId(), checked)); row.addView(selected);
        TextView price = new TextView(requireContext()); price.setText(getString(R.string.cart_item_price_format,
                money.format(item.getUnitPrice()), money.format(item.calculateTotal()))); row.addView(price);
        LinearLayout actions = new LinearLayout(requireContext()); actions.setOrientation(LinearLayout.HORIZONTAL);
        EditText quantity = new EditText(requireContext()); quantity.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); quantity.setText(Double.toString(item.getQuantity()));
        actions.addView(quantity, new LinearLayout.LayoutParams(0, dp(52), 1));
        Button update = new Button(requireContext()); update.setText(R.string.cart_update_action);
        update.setOnClickListener(v -> {
            try { viewModel.updateQuantity(item.getId(), Double.parseDouble(quantity.getText().toString())); }
            catch (NumberFormatException error) { Toast.makeText(requireContext(),
                    R.string.invalid_pricing_quantity, Toast.LENGTH_SHORT).show(); }
        }); actions.addView(update);
        Button remove = new Button(requireContext()); remove.setText(R.string.cart_remove_action);
        remove.setOnClickListener(v -> viewModel.remove(item.getId())); actions.addView(remove);
        row.addView(actions); return row;
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
