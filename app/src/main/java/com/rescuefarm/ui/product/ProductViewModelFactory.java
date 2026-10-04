package com.rescuefarm.ui.product;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.rescuefarm.data.repository.AuthRepositoryFactory;
import com.rescuefarm.data.repository.ProductRepositoryFactory;

public class ProductViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    public ProductViewModelFactory(Context context) { this.context = context.getApplicationContext(); }
    @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(ProductViewModel.class)) {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
        return (T) new ProductViewModel(ProductRepositoryFactory.create(context),
                AuthRepositoryFactory.create(context));
    }
}
