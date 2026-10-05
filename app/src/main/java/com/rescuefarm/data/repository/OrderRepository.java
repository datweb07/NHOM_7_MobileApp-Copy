package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;
import com.rescuefarm.domain.model.Order;
import com.rescuefarm.service.order.CheckoutRequest;
import java.util.List;

public interface OrderRepository {
    enum ErrorCode { VALIDATION, INSUFFICIENT_STOCK, MULTI_SELLER, CAMPAIGN_CONFLICT,
        FORBIDDEN, NETWORK, NOT_FOUND, CONFLICT, UNKNOWN }
    interface OrderCallback {
        void onSuccess(Order order, boolean idempotentReplay);
        void onError(ErrorCode error, String message);
    }
    interface ActionCallback { void onSuccess(); void onError(ErrorCode error, String message); }
    LiveData<List<Order>> observeOrders(String ownerId);
    void checkout(CheckoutRequest request, OrderCallback callback);
    void refreshOrders(String ownerId, ActionCallback callback);
    void findGuestOrder(String orderCode, String receiverPhone, OrderCallback callback);
    default void close() { }
}
