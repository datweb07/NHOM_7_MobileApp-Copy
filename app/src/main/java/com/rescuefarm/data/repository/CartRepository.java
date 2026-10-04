package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.model.Cart;

public interface CartRepository {
    LiveData<Cart> observeCart(String ownerKey);
}
