package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.model.Cart;
import com.rescuefarm.domain.model.CartItem;
import com.rescuefarm.domain.enums.CartOwnerType;

public interface CartRepository {
    enum ErrorCode { NOT_CONFIGURED, NOT_FOUND, VALIDATION, NETWORK, FORBIDDEN, UNKNOWN }
    interface ActionCallback {
        void onSuccess();
        void onError(ErrorCode error, String message);
    }
    LiveData<Cart> observeCart(String ownerKey);
    default LiveData<Cart> observeCart() { return observeCart(getOwnerKey()); }
    CartOwnerType getOwnerType();
    String getOwnerKey();
    void addOrReplace(CartItem item, ActionCallback callback);
    void replace(CartItem item, ActionCallback callback);
    void updateQuantity(String itemId, double quantity, ActionCallback callback);
    void setSelected(String itemId, boolean selected, ActionCallback callback);
    void updateQuote(String itemId, double unitPrice, ActionCallback callback);
    void remove(String itemId, ActionCallback callback);
    void clear(ActionCallback callback);
    void refresh(ActionCallback callback);
    default void close() { }
}
