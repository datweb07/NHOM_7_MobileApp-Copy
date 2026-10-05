package com.rescuefarm.ui.cart;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.rescuefarm.data.repository.CartRepositoryFactory;
import com.rescuefarm.data.repository.ProductRepositoryFactory;

public class CartViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    public CartViewModelFactory(Context context) { this.context = context.getApplicationContext(); }
    @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(CartViewModel.class)) throw new IllegalArgumentException("Unknown ViewModel");
        return (T) new CartViewModel(CartRepositoryFactory.create(context),
                ProductRepositoryFactory.create(context));
    }
}
