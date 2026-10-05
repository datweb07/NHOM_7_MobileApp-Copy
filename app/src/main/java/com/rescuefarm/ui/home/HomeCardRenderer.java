package com.rescuefarm.ui.home;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.rescuefarm.domain.model.Banner;
import com.rescuefarm.domain.model.Category;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.RescueCampaign;
import java.text.NumberFormat;
import java.util.Locale;

public final class HomeCardRenderer {
    private final Fragment fragment;
    public HomeCardRenderer(Fragment fragment) { this.fragment = fragment; }

    public View banner(Banner value, View.OnClickListener listener) {
        MaterialCardView card = card();
        LinearLayout body = body();
        ImageView image = image(150);
        if (!value.getImageUrl().isEmpty()) Glide.with(fragment).load(value.getImageUrl()).centerCrop().into(image);
        body.addView(image); body.addView(text(value.getTitle(), 20, true));
        card.addView(body); card.setOnClickListener(listener); return card;
    }

    public View campaign(RescueCampaign value, double distance, View.OnClickListener listener) {
        MaterialCardView card = card(); LinearLayout body = body();
        body.addView(text(value.getTitle(), 18, true));
        body.addView(text(value.getHighlightLabel(), 13, true));
        body.addView(text(reason(value), 14, false));
        body.addView(text(String.format(Locale.forLanguageTag("vi-VN"),
                "Tiến độ %.0f%% • %.1f / %.1f", value.calculateProgress(),
                value.getRescuedQuantity(), value.getTargetQuantity()), 14, false));
        if (Double.isFinite(distance)) body.addView(text(String.format(Locale.forLanguageTag("vi-VN"),
                "Cách bạn %.1f km", distance), 14, true));
        card.addView(body); card.setOnClickListener(listener); return card;
    }

    public View product(Product value, View.OnClickListener listener) {
        MaterialCardView card = card(); LinearLayout row = body(); row.setOrientation(LinearLayout.HORIZONTAL);
        ImageView image = image(92); row.addView(image, new LinearLayout.LayoutParams(dp(92), dp(92)));
        if (!value.getImageUrls().isEmpty()) Glide.with(fragment).load(value.getImageUrls().get(0)).centerCrop().into(image);
        LinearLayout details = new LinearLayout(fragment.requireContext()); details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);
        details.addView(text(value.getName(), 18, true));
        details.addView(text("GIẢM " + Math.round(value.calculateDiscountPercent()) + "%", 13, true));
        details.addView(text(money(value.getRescuePrice()) + " / " + value.getUnit(), 14, false));
        details.addView(text(value.getOrigin() + " • " + value.getProvince(), 13, false));
        row.addView(details, new LinearLayout.LayoutParams(0, -2, 1)); card.addView(row);
        card.setOnClickListener(listener); return card;
    }

    public View category(Category value, View.OnClickListener listener) {
        MaterialCardView card = card(); LinearLayout body = body();
        if (!value.getImageUrl().isEmpty()) {
            ImageView image = image(96); body.addView(image);
            Glide.with(fragment).load(value.getImageUrl()).centerCrop().into(image);
        }
        body.addView(text(value.getName(), 17, true)); card.addView(body);
        card.setOnClickListener(listener); return card;
    }

    public View message(String value) {
        TextView view = text(value, 14, false); view.setPadding(dp(4), dp(8), dp(4), dp(14)); return view;
    }
    private MaterialCardView card() {
        MaterialCardView card = new MaterialCardView(fragment.requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, dp(12)); card.setLayoutParams(params);
        card.setRadius(dp(16)); card.setStrokeWidth(dp(1)); card.setCardElevation(0); return card;
    }
    private LinearLayout body() {
        LinearLayout body = new LinearLayout(fragment.requireContext()); body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(14), dp(14), dp(14), dp(14)); return body;
    }
    private ImageView image(int height) {
        ImageView image = new ImageView(fragment.requireContext()); image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription("Ảnh nội dung giải cứu");
        image.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(height))); return image;
    }
    private TextView text(String value, int size, boolean bold) {
        TextView view = new TextView(fragment.requireContext()); view.setText(value); view.setTextSize(size);
        if (bold) view.setTypeface(view.getTypeface(), Typeface.BOLD); return view;
    }
    private String reason(RescueCampaign value) {
        return value.getRescueReason().name().replace('_', ' ') + " • "
                + value.getUrgencyLevel().name() + " • " + value.getRescueMode().name().replace('_', ' ');
    }
    private String money(double value) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(value);
    }
    private int dp(int value) {
        return Math.round(value * fragment.getResources().getDisplayMetrics().density);
    }
}
