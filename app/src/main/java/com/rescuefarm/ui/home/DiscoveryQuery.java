package com.rescuefarm.ui.home;

import com.rescuefarm.domain.enums.RescueMode;
import com.rescuefarm.domain.enums.RescueReason;
import com.rescuefarm.domain.enums.UrgencyLevel;

public final class DiscoveryQuery {
    public enum Sort { RELEVANCE, URGENCY, DISCOUNT_DESC, PRICE_ASC, DISTANCE }

    private final String text;
    private final String categoryId;
    private final String province;
    private final RescueReason reason;
    private final UrgencyLevel urgency;
    private final RescueMode mode;
    private final Sort sort;

    public DiscoveryQuery(String text, String categoryId, String province, RescueReason reason,
            UrgencyLevel urgency, RescueMode mode, Sort sort) {
        this.text = clean(text); this.categoryId = clean(categoryId);
        this.province = clean(province); this.reason = reason; this.urgency = urgency;
        this.mode = mode; this.sort = sort == null ? Sort.RELEVANCE : sort;
    }

    public static DiscoveryQuery empty() {
        return new DiscoveryQuery("", "", "", null, null, null, Sort.RELEVANCE);
    }

    private static String clean(String value) { return value == null ? "" : value.trim(); }
    public String getText() { return text; }
    public String getCategoryId() { return categoryId; }
    public String getProvince() { return province; }
    public RescueReason getReason() { return reason; }
    public UrgencyLevel getUrgency() { return urgency; }
    public RescueMode getMode() { return mode; }
    public Sort getSort() { return sort; }
}
