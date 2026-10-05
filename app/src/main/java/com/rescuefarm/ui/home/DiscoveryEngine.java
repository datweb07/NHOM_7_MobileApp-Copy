package com.rescuefarm.ui.home;

import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.UrgencyLevel;
import com.rescuefarm.domain.model.Product;
import com.rescuefarm.domain.model.RescueCampaign;
import com.rescuefarm.service.location.CampaignLocationService;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DiscoveryEngine {
    private final CampaignLocationService locationService = new CampaignLocationService();

    public List<DiscoveryResult> search(List<Product> products, List<RescueCampaign> campaigns,
            DiscoveryQuery query, Double latitude, Double longitude, Date now) {
        DiscoveryQuery safeQuery = query == null ? DiscoveryQuery.empty() : query;
        Date safeNow = now == null ? new Date() : now;
        List<DiscoveryResult> result = new ArrayList<>();
        if (products != null && safeQuery.getReason() == null && safeQuery.getUrgency() == null
                && safeQuery.getMode() == null
                && safeQuery.getSort() != DiscoveryQuery.Sort.DISTANCE) {
            for (Product product : products) if (matchesProduct(product, safeQuery)) {
                result.add(DiscoveryResult.product(product));
            }
        }
        if (campaigns != null && safeQuery.getCategoryId().isEmpty()) {
            for (RescueCampaign campaign : campaigns) if (matchesCampaign(campaign, safeQuery)) {
                double distance = distance(campaign, latitude, longitude, safeNow);
                if (safeQuery.getSort() != DiscoveryQuery.Sort.DISTANCE
                        || Double.isFinite(distance)) {
                    result.add(DiscoveryResult.campaign(campaign, distance));
                }
            }
        }
        result.sort(comparator(safeQuery.getSort()));
        return result;
    }

    private boolean matchesProduct(Product value, DiscoveryQuery query) {
        if (value == null || !value.isAvailable()) return false;
        if (!query.getCategoryId().isEmpty()
                && !query.getCategoryId().equals(value.getCategoryId())) return false;
        if (!query.getProvince().isEmpty()
                && !contains(value.getProvince(), query.getProvince())) return false;
        return query.getText().isEmpty() || contains(value.getName(), query.getText())
                || contains(value.getDescription(), query.getText())
                || contains(value.getOrigin(), query.getText())
                || contains(value.getProvince(), query.getText());
    }

    private boolean matchesCampaign(RescueCampaign value, DiscoveryQuery query) {
        if (value == null || value.getStatus() != com.rescuefarm.domain.enums.CampaignStatus.ACTIVE)
            return false;
        if (query.getReason() != null && query.getReason() != value.getRescueReason()) return false;
        if (query.getUrgency() != null && query.getUrgency() != value.getUrgencyLevel()) return false;
        if (query.getMode() != null && query.getMode() != value.getRescueMode()) return false;
        if (!query.getProvince().isEmpty()
                && !contains(value.getLocationName(), query.getProvince())) return false;
        return query.getText().isEmpty() || contains(value.getTitle(), query.getText())
                || contains(value.getDescription(), query.getText())
                || contains(value.getLocationName(), query.getText())
                || contains(value.getRescueReason().name(), query.getText());
    }

    private double distance(RescueCampaign value, Double latitude, Double longitude, Date now) {
        if (latitude == null || longitude == null) return Double.NaN;
        if (value.getRescueMode() == RescueMode.MOBILE_POINT) {
            return locationService.distanceToCampaign(latitude, longitude, value, now);
        }
        return locationService.distanceKilometers(latitude, longitude,
                value.getLatitude(), value.getLongitude());
    }

    private Comparator<DiscoveryResult> comparator(DiscoveryQuery.Sort sort) {
        Comparator<DiscoveryResult> criticalFirst = Comparator.comparingInt(this::urgencyRank);
        if (sort == DiscoveryQuery.Sort.DISCOUNT_DESC) {
            return Comparator.comparingDouble(this::discount).reversed()
                    .thenComparing(criticalFirst).thenComparing(this::stableTitle,
                            String.CASE_INSENSITIVE_ORDER);
        } else if (sort == DiscoveryQuery.Sort.PRICE_ASC) {
            return Comparator.comparingDouble(this::price).thenComparing(criticalFirst)
                    .thenComparing(this::stableTitle, String.CASE_INSENSITIVE_ORDER);
        } else if (sort == DiscoveryQuery.Sort.DISTANCE) {
            return Comparator.comparingDouble(this::distanceOrMax).thenComparing(criticalFirst)
                    .thenComparing(this::stableTitle, String.CASE_INSENSITIVE_ORDER);
        } else if (sort == DiscoveryQuery.Sort.URGENCY) {
            return criticalFirst.thenComparing(this::stableTitle, String.CASE_INSENSITIVE_ORDER);
        }
        return criticalFirst.thenComparingInt(
                value -> value.getType() == DiscoveryResult.Type.CAMPAIGN ? 0 : 1)
                .thenComparing(this::stableTitle, String.CASE_INSENSITIVE_ORDER);
    }

    private int urgencyRank(DiscoveryResult value) {
        if (value.getCampaign() == null) return 3;
        UrgencyLevel urgency = value.getCampaign().getUrgencyLevel();
        return urgency == UrgencyLevel.CRITICAL ? 0 : urgency == UrgencyLevel.HIGH ? 1 : 2;
    }
    private double discount(DiscoveryResult value) {
        return value.getProduct() == null ? -1D : value.getProduct().calculateDiscountPercent();
    }
    private double price(DiscoveryResult value) {
        return value.getProduct() == null ? Double.MAX_VALUE : value.getProduct().getRescuePrice();
    }
    private double distanceOrMax(DiscoveryResult value) {
        return Double.isFinite(value.getDistanceKilometers())
                ? value.getDistanceKilometers() : Double.MAX_VALUE;
    }
    private String stableTitle(DiscoveryResult value) {
        return value.getProduct() == null ? value.getCampaign().getTitle() : value.getProduct().getName();
    }
    private static boolean contains(String source, String expected) {
        return normalize(source).contains(normalize(expected));
    }
    private static String normalize(String value) {
        String clean = value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
        return Normalizer.normalize(clean, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
    }
}
