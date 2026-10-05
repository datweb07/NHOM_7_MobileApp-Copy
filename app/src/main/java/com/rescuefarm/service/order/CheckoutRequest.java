package com.rescuefarm.service.order;

import com.rescuefarm.domain.enums.FulfillmentType;
import com.rescuefarm.domain.enums.OrderOwnerType;
import com.rescuefarm.domain.enums.PaymentMethod;
import com.rescuefarm.domain.model.CartItem;
import java.util.ArrayList;
import java.util.List;

public final class CheckoutRequest {
    public final String requestId; public final OrderOwnerType ownerType; public final String ownerId;
    public final String receiverName; public final String receiverPhone; public final String receiverAddress;
    public final double latitude; public final double longitude; public final FulfillmentType fulfillmentType;
    public final PaymentMethod paymentMethod; public final String note; public final List<CartItem> items;
    public CheckoutRequest(String requestId, OrderOwnerType ownerType, String ownerId, String receiverName,
            String receiverPhone, String receiverAddress, double latitude, double longitude,
            FulfillmentType fulfillmentType, PaymentMethod paymentMethod, String note, List<CartItem> items) {
        this.requestId = requestId; this.ownerType = ownerType; this.ownerId = ownerId;
        this.receiverName = receiverName; this.receiverPhone = receiverPhone; this.receiverAddress = receiverAddress;
        this.latitude = latitude; this.longitude = longitude; this.fulfillmentType = fulfillmentType;
        this.paymentMethod = paymentMethod; this.note = note; this.items = new ArrayList<>(items);
    }
}
