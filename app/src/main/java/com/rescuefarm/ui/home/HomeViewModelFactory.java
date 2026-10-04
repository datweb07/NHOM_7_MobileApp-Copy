package com.rescuefarm.ui.home;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.rescuefarm.data.repository.AuthRepositoryFactory;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    public HomeViewModelFactory(Context context) { this.context = context.getApplicationContext(); }

    @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!modelClass.isAssignableFrom(HomeViewModel.class)) {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
        return (T) new HomeViewModel(AuthRepositoryFactory.create(context));
    }
}
