package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.model.Order;

import java.util.List;

public interface OrderRepository {
    LiveData<List<Order>> observeOrders(String ownerId);
}
