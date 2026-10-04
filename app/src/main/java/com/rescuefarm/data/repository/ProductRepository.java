package com.rescuefarm.data.repository;

import androidx.lifecycle.LiveData;

import com.rescuefarm.domain.model.Product;

import java.util.List;

public interface ProductRepository {
    LiveData<List<Product>> observeProducts();
    void refreshProducts();
}
